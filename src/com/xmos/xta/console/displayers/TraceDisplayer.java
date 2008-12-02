package com.xmos.xta.console.displayers;

import java.util.ArrayList;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.CodeReference;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class TraceDisplayer implements Displayer {
	
	public enum Level {
	    COMPLETE, 
	    UNRESOLVED,
	    PATHID,   
        ERROR,     
	}

	Console m_console;
	Level m_level;
	int m_pathId;

	public static Level checkLevel(String level) {
		if (level.equals(Level.COMPLETE.toString()))			
		    return Level.COMPLETE;
		else if (level.equals(Level.UNRESOLVED.toString()))
		    return Level.UNRESOLVED;
					
		return Level.ERROR;
	}
	
	public static String getLevelOptionString() {
        return String.format("      level %s: Trace all complete paths\n", 
        		       TraceDisplayer.Level.COMPLETE.toString()) +
	           String.format("      level %s: Trace all unresolves paths", 
	        		   TraceDisplayer.Level.UNRESOLVED.toString());
	}
	
	public TraceDisplayer(Console console, Level level, int pathId) {		
		m_console = console;
		m_level = level;
		m_pathId = pathId;
	}
	
    public void displayRoute(Route route, DebugInfo debugInfo, 
    		MemoryImage memoryImage, SymbolTable symbolTable) { 
    	
    	switch (m_level) {
    	case COMPLETE:
            tracePaths(route.completePaths(), symbolTable, "Completed paths", 
            		route.fromRef(), route.toRef());
    		break;
    		
    	case UNRESOLVED:
            //tracePaths(route.unresolvedPaths(), symbolTable, "Unresolved paths", 
            //		route.fromRef(), route.toRef());
		    break;
		    		    
    	case PATHID:
    		traceSinglePath(route, symbolTable);
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
	
    private void tracePaths(ArrayList<Block> arrayList, SymbolTable symbolTable, 
    		String title, CodeReference fromRef, CodeReference toRef) {
    	
        if (arrayList.size() > 0) {
            displayTitle(title);
        	
            int instrIndex = 0;
            int maxInstructions = getMaxPathLength(arrayList);
            
            while (instrIndex < maxInstructions) {            
                for (Block path : arrayList) { 
                    if (instrIndex < path.getLength()) {
                    	displayInstruction(path, symbolTable, instrIndex);
                    }
                }
                m_console.display("\n");
                ++instrIndex;
            }            
        }
    }

    private void displayInstruction(Block path, SymbolTable symbolTable, int instrIndex) {
        if (instrIndex < path.getLength()) {
         	XtaInstruction instruction = getInstruction(path, instrIndex);
         	String canPause = instruction.canPause() ? "P" : " ";
         	String functionOffsetString = 
         		instruction.getFunctionName() + " + " + instruction.getFunctionOffset();
         	
         	m_console.display(String.format("%-3d %s 0x%05x %-22s %-12s %-15s|",
             		path.getId(),
             		canPause,
               		instruction.getPc(), 
               		functionOffsetString, 
               		instruction.getNmenonic(), 
               		instruction.getOperandString()));                   
    	}	
    }
    
    private void displayTitle(String title) {
    	m_console.display(String.format("%s:\n", title));
    }
        
    private XtaInstruction getInstruction(Block path, int index) {    	    
		//int startIndex = 0;
		int endIndex = 0;
		
		if (endIndex < path.getInstructions(false).size()){
		    return path.getInstructions(false).get(index);
		}
		
        assert false;
        return null;    	
    }

    private int getMaxPathLength(ArrayList<Block> arrayList) {
        int maxPathLength = 0;
        for (Block path : arrayList) {
        	int pathLength = path.getLength();
        	if (pathLength > maxPathLength) {
        		maxPathLength = pathLength;
        	}
        }
        return maxPathLength;
    }
    
    private void traceSinglePath(Route route, SymbolTable symbolTable) {
    	Block path = DisplayerHelper.getPath(route, m_pathId);
    	if (path == null) {
		    m_console.display(String.format("Error: invalid path id: %d\n", m_pathId));        		
		    return;
		}
    	
		for (XtaInstruction instruction : path.getInstructions(false)) {
         	String canPause = instruction.canPause() ? "P" : " ";
         	String functionOffsetString = 
         		instruction.getFunctionName() + " + " + instruction.getFunctionOffset();
         	
         	m_console.display(String.format("%s 0x%05x %-22s %-12s %-15s",
             		canPause,
               		instruction.getPc(), 
               		functionOffsetString, 
               		instruction.getNmenonic(), 
               		instruction.getOperandString()));
         	
          	m_console.display("\n");
		}
    }
}
