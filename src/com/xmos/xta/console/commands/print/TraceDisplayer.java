package com.xmos.xta.console.commands.print;

import java.util.Collection;
import java.util.List;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class TraceDisplayer {
	private Engine m_engine;
	private Console m_console;
	private int m_routeId;
	private double m_currentTime;
		
	public TraceDisplayer(Engine engine, Console console, int routeId) {
		m_engine = engine;
		m_console = console;
		m_routeId = routeId;
	}
	
    public void display() {
		Collection<Node> nodes = m_engine.getRoute(m_routeId).getSubRoutes();	
		for (Node node : nodes) {
			displayInstruction(node);
		}				
    }
    
    private void displayInstruction(Node node) {
    	m_currentTime = 0;
    	
    	List<XtaInstruction> instructions = node.getInstructions();
		for(XtaInstruction instruction : instructions){
			
			String isUnknown = node.isInstructionUnknown() ? "*" : " ";
	     	
	     	String functionOffsetString = 
	     		instruction.getFunctionName() + " + " + instruction.getFunctionOffset();
	     	
	     	m_console.display(String.format("%s %.1f: (%.1fns) 0x%05x %-22s %-12s %s",
	     			isUnknown,
	         		m_currentTime,
	         		node.getInstructionTimeTaken(),
	           		instruction.getPc(), 
	           		functionOffsetString, 
	           		instruction.getNmenonic(), 
	           		instruction.getOperandString()));
	     	
	     	if (instruction.canPause())
	     		m_console.display(" (P)");
	     	
	     	m_currentTime += node.getInstructionTimeTaken();
	      	m_console.display("\n");    	
		}
		m_console.display("\n");    	
    }
}
