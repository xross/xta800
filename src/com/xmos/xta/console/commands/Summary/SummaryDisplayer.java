package com.xmos.xta.console.commands.Summary;

import java.util.ArrayList;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Attributes;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathAttributes;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.Status;
import com.xmos.xta.engine.Unresolved;
import com.xmos.xta.engine.XtaInstruction;

public class SummaryDisplayer {
	public enum Level {
	    simple, 
	    complete, 
	    complete_loops, 
	    complete_blocks, 
	    complete_instructions, 
	    unresolved,
	    unresolved_loops, 
	    unresolved_blocks, 
	    unresolved_instructions, 
	}

	private Engine m_engine;
	private Console m_console;
	private Level m_level;	
		
	public SummaryDisplayer(Engine engine, Console console, Level level) {
		m_engine = engine;
		m_console = console;
		m_level = level;
	}
	
    public void display() { 
		if (!Command.isExecutableLoaded(m_engine, m_console))
			return;			    	

		if (!Command.isRouteAnalysed(m_engine, m_console))
			return;		    	
    	
    	Route route = m_engine.getRoute();
    	
    	switch (m_level) {
    	case complete:
    	case complete_loops:
    	case complete_blocks:
    	case complete_instructions:
    		m_console.display(String.format("Path(id): Timing(ns)/Thread cycles/Instrs/Fnops/Pausable/Executions/Absolute\n"));     			
         	m_console.display(String.format("+COMPLETE [%d path(s)]\n", route.getCompletedPaths().size()));
	        summarisePaths(route.getCompletedPaths(), "    ", null);	        	       
	        break;
    	
        case unresolved:
        case unresolved_loops:
        case unresolved_blocks:
        case unresolved_instructions:
         	m_console.display(String.format("+UNRESOLVED [%d path(s)]\n", route.getUnresolvedPaths().size()));
	        ArrayList<PathBasicBlock> unresolvedPaths = new ArrayList<PathBasicBlock>();
	        for(Unresolved unknown : route.getUnresolvedPaths()){
	        	unresolvedPaths.add(unknown.getBlock());
	        }
         	summarisePaths(unresolvedPaths, "    ", null);
	        break;
	        
        default:
        	assert false;
            break;
    	}    	
    }
	    
    private void summarisePaths(ArrayList<PathBasicBlock> loopPaths, String indent, PathBasicBlock parent) {
        for (PathBasicBlock path : loopPaths) {
            int iterations = 1;
            boolean isAbsolute = false;

            if (parent != null) {
                PathAttributes attributes = parent.getLoopPathAttributes(path);
                iterations = attributes.getNumIterations();
                isAbsolute = attributes.isAbsolute();
            }
                
        	summarisePath(path, indent, iterations, isAbsolute);
        }
    }
    
    private void summarisePath(PathBasicBlock path, String indent, int numExecutions, boolean absolute) {
   	    displayPathLevelInfo(path, indent, numExecutions, absolute);
   	    
        for (PathBasicBlock block : path.getPathBlocks()) {
        	
            if ((m_level == Level.complete_blocks) || 
                (m_level == Level.unresolved_blocks) ||
                (m_level == Level.complete_instructions) || 
                (m_level == Level.unresolved_instructions))
                displayBlockLevelInfo(block, indent);                        	
        	
            if ((m_level == Level.complete_instructions) || 
                (m_level == Level.unresolved_instructions))
                displayInstructionLevelInfo(block, indent, numExecutions);                

            if ((m_level == Level.complete_loops) || 
               	(m_level == Level.complete_blocks) ||
            	(m_level == Level.complete_instructions) ||
            	(m_level == Level.unresolved_loops) || 
            	(m_level == Level.unresolved_blocks) || 
            	(m_level == Level.unresolved_instructions)) {
              	displayLoopLevelInfo(block, indent, numExecutions);          	
            }
        }
        path.doneWithPathBlocks();
    }

    private void displayPathLevelInfo(PathBasicBlock path, String indent, int numExecutions, boolean absolute) {
        
    	
    	String pathId = "";
    	if(path.getPathId()!= -1){
    		pathId = String.format("path(%d)", path.getPathId());
    	} else {
    		pathId = "path(none)";
    	}

        if ((path.getStatus() != Status.BlockStatus.COMPLETE) ) {
            String comment = "";
            comment = path.getUnresolved().getReason();
            m_console.display(String.format("%s%-11s: %s\n", indent, pathId, comment));
        }
        
        m_console.display(String.format("%s%-11s: %.1f/%d/%d/%d/%d/%d/%d\n", 
        		indent, pathId, 
        		path.getTimeTaken(true),
        		path.getNumCycles(true),
        		path.getNumInstructions(true), 
        		path.getNumFNops(true),
        		path.getNumPausables(true),
        		numExecutions, 
        		absolute == true ? 1 : 0));
    }

    private void displayBlockLevelInfo(PathBasicBlock block, String indent) {
        String blockId = String.format("block(b%d)", block.getBlockId());

        m_console.display(String.format("%s    %-11s: %.1f/%d/%d/%d/%d\n", 
        		indent, blockId, 
        		block.getTimeTakenThis(),
        		block.getNumCyclesThis(),
        		block.getNumInstructionsThis(), 
        		block.getNumFNopsThis(),
        		block.getNumPausablesThis()));
    }

    private void displayLoopLevelInfo(PathBasicBlock path, String indent, int numIterations) {
        ArrayList<PathBasicBlock> loopPaths = path.getLoopPaths();
        if (loopPaths.size() > 0) {
            int totalIterations = 0;
            for (PathBasicBlock loopPath : loopPaths) {
                Attributes attributes = path.getLoopPathAttributes(loopPath);
                totalIterations += attributes.getNumIterations();
            }
        	m_console.display(String.format("%s    +loop [%d path(s), %d iteration(s)]\n", 
        			indent, loopPaths.size(), totalIterations));    
            	
        	summarisePaths(loopPaths, indent + "        ", path);
        }
    }

    private void displayInstructionLevelInfo(PathBasicBlock path, String indent, int numExecutions) {
        if ((m_level == Level.complete_instructions) ||
            (m_level == Level.unresolved_instructions)) {
        	
            for (XtaInstruction xtaInstruction : path.getInstructionStream()) {
                m_console.display(String.format("%s        (%.1fns) 0x%05x %s %s %s",
                	indent,
                	xtaInstruction.getTimeTaken(),
                	xtaInstruction.getPc(),
                	xtaInstruction.getNmenonic(),
                	xtaInstruction.getOperandString(),
                	xtaInstruction.canPause() ? "(P)" : ""));
                                
                m_console.display("\n");
            }
        }
    }
}
