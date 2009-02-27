package com.xmos.xta.console.commands.Print;

import java.util.Vector;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.XtaInstruction;

public class TraceDisplayer {
	private Engine m_engine;
	private Console m_console;
	private int m_id;
	private Command.IdType m_idType;
		
	public TraceDisplayer(Engine engine, Console console, int id, Command.IdType idType) {
		m_engine = engine;
		m_console = console;
		m_id = id;
		m_idType = idType;
	}
	
    public void display() {  
        Vector<XtaInstruction> instructions = null;    		
        switch (m_idType) {
        case BLOCK:
        	PathBasicBlock block = m_engine.getRoute().getBlock(m_id);			
        	instructions = block.getInstructionsThis();
        	break;
        case PATH:
        	PathBasicBlock path = m_engine.getRoute().getPath(m_id);			
        	instructions = path.getInstructions(false);
        	break;
        default:
        	assert(false);
        }
        	
    	double currentTime = 0.0;
		for (XtaInstruction instruction : instructions) {
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
