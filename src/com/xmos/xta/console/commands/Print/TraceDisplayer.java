package com.xmos.xta.console.commands.Print;

import java.util.LinkedList;
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
	private double m_currentTime;
		
	public TraceDisplayer(Engine engine, Console console, int routeId, int id, Command.IdType idType) {
		m_engine = engine;
		m_console = console;
		m_routeId = routeId;
		m_id = id;
		m_idType = idType;
		m_currentTime = 0.0;
	}
	
    public void display() {
        switch (m_idType) {
        case BLOCK:
        {
        	PathBasicBlock block = m_engine.getRoute(m_routeId).getBlock(m_id);
        	this.displayBlock(block);
        	break;
        }
        case PATH:
        {
        	PathBasicBlock path = m_engine.getRoute(m_routeId).getPath(m_id);
    		displayPath(path);
        	break;
        }
        default:
        	assert(false);
        }
    }

    private void displayPath(PathBasicBlock path) {
    	for (PathBasicBlock block : path.getPathBlocks()) {
    		for (PathBasicBlock loopPath : block.getLoopPaths(path)) {
    			for (int i = 0; i < loopPath.getNumIterations(); i++) {
    				displayPath(loopPath);
    			}
    		}
    		displayBlock(block);
    	}
	}

	public void displayBlock(PathBasicBlock block) {
    	LinkedList<XtaInstruction> instructions = block.getInstructionStream();
        	
		for (XtaInstruction instruction : instructions) {
			String isUnknown = instruction.isUnknown() ? "*" : " ";
         	
         	String functionOffsetString = 
         		instruction.getFunctionName() + " + " + instruction.getFunctionOffset();
         	
         	m_console.display(String.format("%s %.1f: (%.1fns) 0x%05x %-22s %-12s %s",
         			isUnknown,
             		m_currentTime,
             		instruction.getTimeTaken(),
               		instruction.getPc(), 
               		functionOffsetString, 
               		instruction.getNmenonic(), 
               		instruction.getOperandString()));
         	
         	if (instruction.canPause())
         		m_console.display(" (P)");
         	
         	m_currentTime += instruction.getTimeTaken();
          	m_console.display("\n");
		}  		
    }
}
