package com.xmos.xta.console.displayers;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class TraceDisplayer implements Displayer {
	private Console m_console;
	private int m_pathId;
		
	public TraceDisplayer(Console console, int pathId) {		
		m_console = console;
		m_pathId = pathId;
	}
	
    public void displayRoute(Route route, DebugInfo debugInfo, 
    		MemoryImage memoryImage, SymbolTable symbolTable) {     	
  		
    	Block path = DisplayerHelper.getPath(route, m_pathId);
    	if (path == null) {
		    m_console.display(String.format("Error: invalid path id: %d\n", m_pathId));        		
		    return;
		}
    	
		for (XtaInstruction instruction : path.getInstructions(false)) {
         	String canPause = instruction.canPause() ? "P" : " ";
         	String functionOffsetString = 
         		instruction.getFunctionName() + " + " + instruction.getFunctionOffset();
         	
         	m_console.display(String.format("%s (%.1fns) 0x%05x %-22s %-12s %-15s",
             		canPause,
             		instruction.getTimeTaken(),
               		instruction.getPc(), 
               		functionOffsetString, 
               		instruction.getNmenonic(), 
               		instruction.getOperandString()));
         	
          	m_console.display("\n");
		}  		
    }

	public void notify(String message, String argument) {
	}	        
}
