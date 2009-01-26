package com.xmos.xta.console.displayers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.dwarfhandler.DebugFile;
import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.isa.Instruction;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Attributes;
import com.xmos.xta.engine.PathAttributes;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.Status;
import com.xmos.xta.engine.Unresolved;
import com.xmos.xta.engine.XtaInstruction;

public class SummaryDisplayer implements Displayer {
	public enum Level {
	    simple, 
	    complete, 
	    complete_loops, 
	    complete_instructions, 
	    unresolved,
	    unresolved_loops, 
	    unresolved_instructions, 
	    functions,
	    sources,           
	    pathid,   
        error,     
	}

	Console m_console;
	Level m_level;
	int m_pathId;
	XeObject m_xeObject;
	
	public static Level checkLevel(String level) {
		if (level.equals(Level.simple.toString()))			
		    return Level.simple;
		else if (level.equals(Level.complete.toString()))
		    return Level.complete;
		else if (level.equals(Level.complete_loops.toString()))
		    return Level.complete_loops;
		else if (level.equals(Level.complete_instructions.toString()))
		    return Level.complete_instructions;
		else if (level.equals(Level.unresolved.toString()))
		    return Level.unresolved;
		else if (level.equals(Level.unresolved_loops.toString()))
		    return Level.unresolved_loops;
		else if (level.equals(Level.unresolved_instructions.toString()))
		    return Level.unresolved_instructions;
		else if (level.equals(Level.functions.toString()))
		    return Level.functions;
		else if (level.equals(Level.sources.toString()))
		    return Level.sources;
					
		return Level.error;
	}
	
	public static String getLevelOptionString() {
        return String.format("      level: %s: Show simple paths summary\n", 
        		SummaryDisplayer.Level.simple.toString()) +
	           String.format("      level: %s: Show complete path(s) timing\n", 
	        		   SummaryDisplayer.Level.complete.toString()) +
	           String.format("      level: %s: Show complete path(s) + loop timing\n", 
	        		   SummaryDisplayer.Level.complete_loops.toString()) +   	
               String.format("      level: %s: Show complete path(s) + loop + basic block + instruction timing\n", 
            		   SummaryDisplayer.Level.complete_instructions.toString()) +      	
	           String.format("      level: %s: Show unresolved path(s) timing\n", 
	        		   SummaryDisplayer.Level.unresolved.toString()) +
	           String.format("      level: %s: Show unresolved path(s) + loop timing\n", 
	        		   SummaryDisplayer.Level.unresolved_loops.toString()) +   	 	
	           String.format("      level: %s: Show unresolved path(s) + loop + basic block + instruction timing\n", 
	        		   SummaryDisplayer.Level.unresolved_instructions.toString()) +   	
	           String.format("      level: %s: Show path(s) + functions summary\n", 
	        		   SummaryDisplayer.Level.functions.toString()) +   	            		   
	           String.format("      level: %s: Show path(s) + sources summary", 
	        		   SummaryDisplayer.Level.sources.toString());      			
	}
	
	public SummaryDisplayer(Console console, Level level, int pathId, XeObject xeObject) {
		m_console = console;
		m_level = level;
		m_pathId = pathId;
		m_xeObject=xeObject;
	}
	
    public void displayRoute(Route route, DebugInfo debugInfo, 
    		MemoryImage memoryImage, SymbolTable symbolTable) {         	
    	switch (m_level) {
    	case simple:
    	    displaySimple(route);
    	    break;
    	        
    	case complete:
    	case complete_loops:
    	case complete_instructions:
    		m_console.display(String.format("Path(id): Timing(ns)/Thread cycles/Instrs/Fnops/Pausable/Executions/Absolute\n"));     			
         	m_console.display(String.format("+COMPLETE [%d path(s)]\n", route.completePaths().size()));
	        summarisePaths(route.completePaths(), "    ", null);	        	       
	        break;
    	
        case unresolved:
        case unresolved_loops:
        case unresolved_instructions:
         	m_console.display(String.format("+UNRESOLVED [%d path(s)]\n", route.unresolvedPaths().size()));
	        ArrayList<Block> unresolvedPaths = new ArrayList<Block>();
	        for(Unresolved unknown : route.unresolvedPaths()){
	        	unresolvedPaths.add(unknown.getBlock());
	        }

         	summarisePaths(unresolvedPaths, "    ", null);
	        break;
	        
        case functions:
        	displayFunctions(route, symbolTable);
        	break;

        case sources:
        	displaySources(route, debugInfo);
        	break;
        	
        case pathid:
        	displayPathSummary(route);
        	break;
        	
        default:
        	assert false;
            break;
    	}    	
    }

	public void notify(String message, String argument) {
	}
	
    private void displaySimple(Route route) {
        m_console.display(String.format("Complete paths:   %d\n", route.completePaths().size()));
        m_console.display(String.format("Unresolved paths: %d\n", route.unresolvedPaths().size()));
    }
    
    private void summarisePaths(ArrayList<Block> loopPaths, String indent, Block parent) {
        for (Block path : loopPaths) {
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

            if ((m_level == Level.complete_instructions) || 
                    (m_level == Level.unresolved_instructions))
                displayInstructionLevelInfo(block, indent, numExecutions);                

            if ((m_level == Level.complete_loops) || 
            	(m_level == Level.complete_instructions) ||
            	(m_level == Level.unresolved_loops) || 
            	(m_level == Level.unresolved_instructions)) {
              	displayLoopLevelInfo(block, indent, numExecutions);          	
            }
        }
        path.doneWithPathBlocks();
    }
        
    private void displayPathLevelInfo(Block path, String indent, int numExecutions, boolean absolute) {
        String pathId = String.format("path(%d)", path.getId());

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
    
    private void displayLoopLevelInfo(Block path, String indent, int numIterations) {
        ArrayList<Block> loopPaths = path.getLoopPaths();
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
    }

    private void displayInstructionLevelInfo(Block path, String indent, int numExecutions) {
        if ((m_level == Level.complete_instructions) ||
            (m_level == Level.unresolved_instructions)) {
        	
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
  	    for (DebugFile debugFile : debugInfo.getDebugEngine().getDebugFiles()) {
        	TreeSet<Integer> pathIds = new TreeSet<Integer>();
            for (Block path : route.completePaths()) {
            	buildPathsWithinSourcesSet(path, pathIds, debugFile.getFileName());
            }    	
            
            if (pathIds.size() > 0) {
            	m_console.display(String.format("%s: ", debugFile.getFileName())); 
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
        }
        path.doneWithPathBlocks();        
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
    }
    
    private boolean pathWithinSourceFile(Block path, String sourceFileName) {
        //TODO make this faster
        HashMap<String, TreeSet<Integer>> linesCovered = new HashMap<String, TreeSet<Integer>>();
        TreeSet<Integer> bob = new TreeSet<Integer>();
        for(XtaInstruction inst : path.getInstructions(false)){
            for(SourceFile sourceFile: m_xeObject.getSourceFiles()){
                if(sourceFile.getFile().getName().equals(sourceFileName)){
                    for(SourceLine sourceLine : sourceFile.getSourceLines()){
                        for(Line assembly :sourceLine.getChildern()){
                            if(assembly.getLineNumber() == inst.getPc()){
                                bob.add(sourceLine.getLineNumber());
                            }
                        }
                    }
                }
            }
        }
        linesCovered.put(sourceFileName, bob);
        
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
        m_console.display(String.format("Num Blocks:       %d\n", path.getNumPathBlocks()));
        m_console.display(String.format("Num Pauseables:   %d\n", path.getNumPausables(true)));
        
        int numLoops = 0;
        for (Block block : path.getPathBlocks()) {
            numLoops += block.numLoopPaths();
        }
        path.doneWithPathBlocks();
        m_console.display(String.format("Num Loops:        %d\n", numLoops));
    }
}
