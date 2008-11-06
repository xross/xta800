package com.xmos.xta.console.displayers;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.isa.Instruction;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Attributes;
import com.xmos.xta.engine.PathAttributes;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class SummaryDisplayer implements Displayer {
	public enum Level {
	    SIMPLE, 
	    COMPLETE, 
	    COMPLETE_LOOPS, 
	    COMPLETE_INSTRUCTIONS, 
	    UNRESOLVED,
	    UNRESOLVED_LOOPS, 
	    UNRESOLVED_INSTRUCTIONS, 
	    FUNCTIONS,
	    SOURCES,           
	    PATHID,   
        ERROR,     
	}

	Console m_console;
	Level m_level;
	int m_pathId;
 
	public static Level checkLevel(String level) {
		if (level.equals(Level.SIMPLE.toString()))			
		    return Level.SIMPLE;
		else if (level.equals(Level.COMPLETE.toString()))
		    return Level.COMPLETE;
		else if (level.equals(Level.COMPLETE_LOOPS.toString()))
		    return Level.COMPLETE_LOOPS;
		else if (level.equals(Level.COMPLETE_INSTRUCTIONS.toString()))
		    return Level.COMPLETE_INSTRUCTIONS;
		else if (level.equals(Level.UNRESOLVED.toString()))
		    return Level.UNRESOLVED;
		else if (level.equals(Level.UNRESOLVED_LOOPS.toString()))
		    return Level.UNRESOLVED_LOOPS;
		else if (level.equals(Level.UNRESOLVED_INSTRUCTIONS.toString()))
		    return Level.UNRESOLVED_INSTRUCTIONS;
		else if (level.equals(Level.FUNCTIONS.toString()))
		    return Level.FUNCTIONS;
		else if (level.equals(Level.SOURCES.toString()))
		    return Level.SOURCES;
					
		return Level.ERROR;
	}
	
	public static String getLevelOptionString() {
        return String.format("      level: %s: Show simple paths summary\n", 
        		SummaryDisplayer.Level.SIMPLE.toString()) +
	           String.format("      level: %s: Show complete path(s) timing\n", 
	        		   SummaryDisplayer.Level.COMPLETE.toString()) +
	           String.format("      level: %s: Show complete path(s) + loop timing\n", 
	        		   SummaryDisplayer.Level.COMPLETE_LOOPS.toString()) +   	
               String.format("      level: %s: Show complete path(s) + loop + basic block + instruction timing\n", 
            		   SummaryDisplayer.Level.COMPLETE_INSTRUCTIONS.toString()) +      	
	           String.format("      level: %s: Show unresolved path(s) timing\n", 
	        		   SummaryDisplayer.Level.UNRESOLVED.toString()) +
	           String.format("      level: %s: Show unresolved path(s) + loop timing\n", 
	        		   SummaryDisplayer.Level.UNRESOLVED_LOOPS.toString()) +   	 	
	           String.format("      level: %s: Show unresolved path(s) + loop + basic block + instruction timing\n", 
	        		   SummaryDisplayer.Level.UNRESOLVED_INSTRUCTIONS.toString()) +   	
	           String.format("      level: %s: Show path(s) + functions summary\n", 
	        		   SummaryDisplayer.Level.FUNCTIONS.toString()) +   	            		   
	           String.format("      level: %s: Show path(s) + sources summary", 
	        		   SummaryDisplayer.Level.SOURCES.toString());      			
	}
	
	public SummaryDisplayer(Console console, Level level, int pathId) {
		m_console = console;
		m_level = level;
		m_pathId = pathId;
	}
	
    public void displayRoute(Route route, DebugInfo debugInfo, 
    		MemoryImage memoryImage, SymbolTable symbolTable) {         	
    	switch (m_level) {
    	case SIMPLE:
    	    displaySimple(route);
    	    break;
    	        
    	case COMPLETE:
    	case COMPLETE_LOOPS:
    	case COMPLETE_INSTRUCTIONS:
    		m_console.display(String.format("Path(id): Timing(ns)/Thread cycles/Instrs/Fnops/Pausable/Executions/Absolute\n"));     			
         	m_console.display(String.format("+COMPLETE [%d path(s)]\n", route.completePaths().size()));
	        summarisePaths(route.completePaths(), "    ", null);	        	       
	        break;
    	
        case UNRESOLVED:
        case UNRESOLVED_LOOPS:
        case UNRESOLVED_INSTRUCTIONS:
         	m_console.display(String.format("+UNRESOLVED [%d path(s)]\n", route.unresolvedPaths().size()));
	        summarisePaths(route.unresolvedPaths(), "    ", null);
	        break;
	        
        case FUNCTIONS:
        	displayFunctions(route, symbolTable);
        	break;

        case SOURCES:
        	displaySources(route, debugInfo);
        	break;
        	
        case PATHID:
        	displayPathSummary(route);
        	break;
        	
        default:
        	assert false;
            break;
    	}    	
    }

	public void notifyAnalysePaths() {
	}

	public void notifyLoadExecutable() {
	}	       

    public void notifyUpdateState() {    	
    }
	
    private void displaySimple(Route route) {
        if (route.functionName() != null)
            m_console.display(String.format("Route: %s\n", route.functionName()));
        else
            m_console.display(String.format("Route: %s -> %s\n", 
                route.fromRef().getRef(), route.toRef().getRef()));
        
        m_console.display(String.format("Complete paths:   %d\n", route.completePaths().size()));
        m_console.display(String.format("Unresolved paths: %d\n", route.unresolvedPaths().size()));
    }
    
    private void summarisePaths(Vector<Block> paths, String indent, Block parent) {
        for (Block path : paths) {
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
    
    private void summarisePath(Block path, String indent, int numExecutions, boolean absolute) {
   	    displayPathLevelInfo(path, indent, numExecutions, absolute);
   	    
        for (Block block : path.getPathBlocks()) {

            if ((m_level == Level.COMPLETE_INSTRUCTIONS) || 
                    (m_level == Level.UNRESOLVED_INSTRUCTIONS))
                displayInstructionLevelInfo(block, indent, numExecutions);                

            if ((m_level == Level.COMPLETE_LOOPS) || 
            	(m_level == Level.COMPLETE_INSTRUCTIONS) ||
            	(m_level == Level.UNRESOLVED_LOOPS) || 
            	(m_level == Level.UNRESOLVED_INSTRUCTIONS)) {
              	displayLoopLevelInfo(block, indent, numExecutions);          	
            }
        }
    }
        
    private void displayPathLevelInfo(Block path, String indent, int numExecutions, boolean absolute) {
        String pathInstanceId = String.format("pathi(%d)", path.getInstanceId());

        if ((path.getStatus() != Block.Status.COMPLETE) && (path.getStatus() != Block.Status.LOOPED)) {
            String comment = "";
            switch (path.getStatus()) {
            case RECURSION:
                comment = "found recursion";
                break;
            case UNRESOLVED:
                comment = "found unresolved PC";
                break;
            }
            m_console.display(String.format("%s%-11s: %s\n", indent, pathInstanceId, comment));
        }
        
        m_console.display(String.format("%s%-11s: %.1f/%d/%d/%d/%d/%d/%d, path(%d)\n", 
        		indent, pathInstanceId, 
        		path.getTimeTaken(true),
        		path.getNumCycles(true),
        		path.getNumInstructions(true), 
        		path.getNumFNops(true),
        		path.getNumPausables(true),
        		numExecutions, 
        		absolute == true ? 1 : 0,
        		path.getId()));
    }
    
    private void displayLoopLevelInfo(Block path, String indent, int numIterations) {
        Vector<Block> loopPaths = path.getLoopPaths();
        if (loopPaths.size() > 0) {
            int totalIterations = 0;
            for (Block loopPath : loopPaths) {
                Attributes attributes = path.getLoopPathAttributes(loopPath);
                totalIterations += attributes.getNumIterations();
            }
        	m_console.display(String.format("%s    +loop [%d path(s), %d iteration(s)]\n", 
        			indent, loopPaths.size(), totalIterations));    
            	
        	summarisePaths(loopPaths, indent + "        ", path);
        }
        path.doneWithLoopPaths();
    }

    private void displayInstructionLevelInfo(Block path, String indent, int numExecutions) {
        if ((m_level == Level.COMPLETE_INSTRUCTIONS) ||
            (m_level == Level.UNRESOLVED_INSTRUCTIONS)) {
        	
            for (XtaInstruction xtaInstruction : path.getInstructionsThis()) {
                m_console.display(String.format("%s    (%.1fns) 0x%05x %s %s %s",
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

    private void displayFunctions(Route route, SymbolTable symbolTable) {
		ElfHandler elfHandler = symbolTable.getElfHandler();        		    
    	Iterator<Map.Entry<Integer, Symbol>> iter = 
    		elfHandler.getFunctionsPcKeyed().entrySet().iterator();
    	    	
    	while (iter.hasNext()) {
    	    Map.Entry<Integer, Symbol> pair = iter.next();
    	    Symbol symbol = pair.getValue();       	    	        	    	   
    	    int functionAddress = symbol.getValue();
    	    int functionSize = symbolTable.functionSize(functionAddress);
    	    
        	TreeSet<Integer> pathIds = new TreeSet<Integer>();
            for (Block path : route.completePaths()) {
            	buildPathsWithinFunctionSet(path, pathIds, functionAddress, functionSize);
            }    	
            
            if (pathIds.size() > 0) {
            	m_console.display(String.format("%s: ", symbol.getName())); 
            	for (Integer pathId : pathIds) {
            		m_console.display(String.format("%d ", pathId));
            	}
            	m_console.display("\n");
            }
    	}
    }

    private void displaySources(Route route, DebugInfo debugInfo) {
    	Collection<File> sourceFiles = debugInfo.getDebugEngine().findFiles("");
        
    	for (File sourceFile : sourceFiles) {
            String sourceFileName = sourceFile.getName();

        	TreeSet<Integer> pathIds = new TreeSet<Integer>();
            for (Block path : route.completePaths()) {
            	buildPathsWithinSourcesSet(path, pathIds, sourceFileName);
            }    	
            
            if (pathIds.size() > 0) {
            	m_console.display(String.format("%s: ", sourceFileName)); 
            	for (Integer pathId : pathIds) {
            		m_console.display(String.format("%d ", pathId));
            	}
            	m_console.display("\n");
            }            
    	}
    }   

    private void buildPathsWithinFunctionSet(Block path, Set<Integer> pathSet, 
    		int functionAddress, int functionSize) {   
    	
        for (Block block : path.getPathBlocks()) {
            if (pathWithinFunction(block, functionAddress, functionSize)) {
                if (!pathSet.contains(path.getId())) {
                    pathSet.add(path.getId());
                }
            }
        
            for (Block loopPath : block.getLoopPaths()) {
   		    	buildPathsWithinFunctionSet(loopPath, pathSet, functionAddress, functionSize);
       		}
            block.doneWithLoopPaths();
        }
    }
    
    private boolean pathWithinFunction(Block path, int functionAddress, int functionSize) {
      	for (Instruction instruction : path.getInstructions(false)) {
        	if ((instruction.getPc() >= functionAddress) &&
        	    (instruction.getPc() < (functionAddress + functionSize))) {
        	    return true;
        	}
        }
        return false;
    }    	    
    
    private void buildPathsWithinSourcesSet(Block path, Set<Integer> pathSet, String sourceFileName) {	
        if (pathWithinSourceFile(path, sourceFileName)) {
        	pathSet.add(path.getId());
        }

        for (Block loopPath : path.getLoopPaths()) {
	    	buildPathsWithinSourcesSet(loopPath, pathSet, sourceFileName);
   		}
        path.doneWithLoopPaths();
    }
    
    private boolean pathWithinSourceFile(Block path, String sourceFileName) {
        HashMap<String, TreeSet<Integer>> linesCovered = path.getSourceLinesCovered();
        if (linesCovered.containsKey(sourceFileName) && 
    		(linesCovered.get(sourceFileName).size() > 0)) {
        	return true;
        }
        return false;
    }   
    
    private void displayPathSummary(Route route) {
        for (Block path : route.completePaths()) {
        	if (path.getId() == m_pathId) {
        		displayPathSummary(path);
        		return;
        	}
	   	}                   
        m_console.display(String.format("Path: %d not found\n", m_pathId));    	
    }

    private void displayPathSummary(Block path) {
        m_console.display(String.format("Timing(ns):       %.1f\n", path.getTimeTaken(true)));
        m_console.display(String.format("Thread cycles:    %d\n", path.getNumCycles(true)));
        m_console.display(String.format("Num Instructions: %d\n", path.getNumInstructions(true)));
        m_console.display(String.format("Num FNops:        %d\n", path.getNumFNops(true)));
        m_console.display(String.format("Num Blocks:       %d\n", path.getPathBlocks().size()));
        m_console.display(String.format("Num Pauseables:   %d\n", path.getNumPausables(true)));
        
        int numLoops = 0;
        for (Block block : path.getPathBlocks()) {
            numLoops += block.numLoopPaths();
        }
        m_console.display(String.format("Num Loops:        %d\n", numLoops));
    }
}
