package com.xmos.xta.console.commands.print;

import java.util.ArrayList;
import java.util.List;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.Status;
import com.xmos.xta.engine.Unresolved;
import com.xmos.xta.engine.XtaInstruction;

public class TreeDisplayer {
	public enum Level {
	    complete_paths, 
	    complete_loops, 
	    complete_blocks, 
	    complete_instructions, 
	    unresolved_paths, 
	    unresolved_loops, 
	    unresolved_blocks, 
	    unresolved_instructions, 
	}

	private Engine m_engine;
	private Console m_console;
	private Level m_level;	
		
	public TreeDisplayer(Engine engine, Console console, Level level) {
		m_engine = engine;
		m_console = console;
		m_level = level;
	}
	
    public void display() { 
		if (!Command.isExecutableLoaded(m_engine, m_console))
			return;			    	

		if (!Command.validateRoutes(m_engine, m_console))
			return;		    	
    	
		for (Route route : m_engine.getRoutes()) {
	    	switch (m_level) {
	    	case complete_paths:
	    	case complete_loops:
	    	case complete_blocks:
	    	case complete_instructions:
	    		m_console.display(String.format("Route(%d) %s\n", 
	    				route.getRouteId(), route.getDescription()));	    		
	         	m_console.display(String.format("Complete [%d path%s]\n", 
	         			route.getCompletedPaths().size(), 
	         			route.getCompletedPaths().size() == 1 ? "" : "s"));
		        summarisePaths(route.getCompletedPaths(), "  ", null);
		        m_console.display("\n");
		        break;
	    	
	        case unresolved_paths:
	        case unresolved_loops:
	        case unresolved_blocks:
	        case unresolved_instructions:
	    		m_console.display(String.format("Route(%d) %s\n", 
	    				route.getRouteId(), route.getDescription()));     			
	         	m_console.display(String.format("Unresolved [%d path%s]\n", 
	         			route.getUnresolvedPaths().size(),
	         			route.getUnresolvedPaths().size() == 1 ? "" : "s"));
		        ArrayList<PathBasicBlock> unresolvedPaths = new ArrayList<PathBasicBlock>();
		        for(Unresolved unknown : route.getUnresolvedPaths()){
		        	unresolvedPaths.add(unknown.getBlock());
		        }
	         	summarisePaths(unresolvedPaths, "  ", null);
		        m_console.display("\n");
		        break;
		        
	        default:
	        	assert false;
	            break;
	    	}    				
		}
    }
	        
    private void summarisePaths(List<PathBasicBlock> loopPaths, String indent, PathBasicBlock parent) {
        for (PathBasicBlock path : loopPaths) {
            long iterations = 1;
            boolean isAbsolute = false;

            if (parent != null) {
            	try {
					iterations = parent.getLoops().getLoopIterations(path);
				} catch (Exception e) {
					iterations += 0;
				}
            	try {
					isAbsolute = parent.getLoops().isAbsolute(path);
				} catch (Exception e) {
					//TODO assume is not absolute if it is not set - wont effect anything as there will be no iterations 
					isAbsolute = false;
				}
            }  
        	summarisePath(path, indent, iterations, isAbsolute);
        }
    }
    
    private void summarisePath(PathBasicBlock path, String indent, long numExecutions, boolean absolute) {
   	    displayPathLevelInfo(path, indent, numExecutions, absolute);
   	    
        for (PathBasicBlock block : path.getPathBlocks()) {
        	if(block.getNumInstructionsThis() >0){
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
	              	displayLoopLevelInfo(block, indent, numExecutions, path);          	
	            }
        	}
        }
    }

    private void displayPathLevelInfo(PathBasicBlock path, String indent, long numExecutions, boolean absolute) {            	
    	String pathId = "";
    	if(path.getPathId()!= -1){
    		pathId = String.format("path(%d)", path.getPathId());
    	} else {
    		pathId = "path(none)";
    	}

        if ((path.getStatus() != Status.BlockStatus.COMPLETE) ) {
            String comment = "";
            comment = path.getUnresolved().getReason();
            m_console.display(String.format("%s  %-11s: %s\n", indent, pathId, comment));
        }
        
		String unknowns = (path.getNumUnknowns(true) > 0) ? "*" : " ";        
        m_console.display(String.format("%s%s %-11s: %.1fns\n", 
        		indent, unknowns, pathId, path.getTimeTaken(true)));
    }

    private void displayBlockLevelInfo(PathBasicBlock block, String indent) {
        String blockId = String.format("block(b%d)", block.getBlockId());

		String unknowns = (block.getNumUnknownsThis() > 0) ? "*" : " ";        
        m_console.display(String.format("%s    %s %-11s: %.1fns\n", 
        		indent, unknowns, blockId, block.getTimeTakenThis()));
    }

    private void displayLoopLevelInfo(PathBasicBlock path, String indent, long numIterations, PathBasicBlock base) {
        List<PathBasicBlock> loopPaths = path.getLoopPaths(base);
        if (loopPaths.size() > 0) {
            long totalIterations = 0;
            for (PathBasicBlock loopPath : loopPaths) {
                try {
					totalIterations += path.getLoops().getLoopIterations(loopPath);
				} catch (Exception e) {
					totalIterations += 0;
				}
            }
        	m_console.display(String.format("%s      loop [%d path%s, %d iteration%s]\n", indent, 
        			loopPaths.size(), (loopPaths.size() == 1 ? "" : "s"),
        			totalIterations, (totalIterations == 1 ? "" : "s")));

        	summarisePaths(loopPaths, indent + "        ", path);
        }
    }

    private void displayInstructionLevelInfo(PathBasicBlock path, String indent, long numExecutions) {
        if ((m_level == Level.complete_instructions) ||
            (m_level == Level.unresolved_instructions)) {
        	
            for (XtaInstruction xtaInstruction : path.getInstructionStream()) {
    			String isUnknown = xtaInstruction.isUnknown() ? "*" : " ";
            	
                m_console.display(String.format("%s        %s (%.1fns) 0x%05x %s %s %s",
                	indent, isUnknown, 
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
