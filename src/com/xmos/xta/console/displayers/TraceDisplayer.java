package com.xmos.xta.console.displayers;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.XtaInstruction;

public class TraceDisplayer {
	private Engine m_engine;
	private Console m_console;
	private int m_pathId;
		
	public TraceDisplayer(Engine engine, Console console, int pathId) {
		m_engine = engine;
		m_console = console;
		m_pathId = pathId;
	}
	
    public void display() {     	  		
    	Block path = Block.getPath(m_engine.getRoute(), m_pathId);
    	
    	double currentTime = 0.0;
		for (XtaInstruction instruction : path.getInstructions(false)) {
         	String canPause = instruction.canPause() ? "P" : " ";
         	String functionOffsetString = 
         		instruction.getFunctionName() + " + " + instruction.getFunctionOffset();
         	
         	m_console.display(String.format("%s %.1f: (%.1fns) 0x%05x %-22s %-12s %s",
             		canPause,
             		currentTime,
             		instruction.getTimeTaken(),
               		instruction.getPc(), 
               		functionOffsetString, 
               		instruction.getNmenonic(), 
               		instruction.getOperandString()));
         	
         	currentTime += instruction.getTimeTaken();
          	m_console.display("\n");
		}  		
    }
}
