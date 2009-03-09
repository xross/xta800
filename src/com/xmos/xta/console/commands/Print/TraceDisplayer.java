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
	private int m_routeId;
	private int m_id;
	private Command.IdType m_idType;
		
	public TraceDisplayer(Engine engine, Console console, int routeId, int id, Command.IdType idType) {
		m_engine = engine;
		m_console = console;
		m_routeId = routeId;
		m_id = id;
		m_idType = idType;
	}
	
    public void display() {  
        Vector<XtaInstruction> instructions = null;    		
        switch (m_idType) {
        case BLOCK:
        	PathBasicBlock block = m_engine.getRoute(m_routeId).getBlock(m_id);			
        	instructions = new Vector<XtaInstruction>( block.getInstructionStream());
        	break;
        case PATH:
        	PathBasicBlock path = m_engine.getRoute(m_routeId).getPath(m_id);			
        	instructions = path.getInstructions(false);
        	break;
        default:
        	assert(false);
        }
        	
    	double currentTime = 0.0;
		for (XtaInstruction instruction : instructions) {
			String isUnknown = instruction.isUnknown() ? "*" : " ";
         	
         	String functionOffsetString = 
         		instruction.getFunctionName() + " + " + instruction.getFunctionOffset();
         	
         	m_console.display(String.format("%s %.1f: (%.1fns) 0x%05x %-22s %-12s %s",
         			isUnknown,
             		currentTime,
             		instruction.getTimeTaken(),
               		instruction.getPc(), 
               		functionOffsetString, 
               		instruction.getNmenonic(), 
               		instruction.getOperandString()));
         	
         	if (instruction.canPause())
         		m_console.display(" (P)");
         	
         	currentTime += instruction.getTimeTaken();
          	m_console.display("\n");
		}  		
    }
}
