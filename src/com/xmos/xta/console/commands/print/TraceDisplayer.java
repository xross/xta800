package com.xmos.xta.console.commands.print;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

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
    	
    	List<InstructionNode> instructions = new LinkedList<InstructionNode>();
    	node.getInstructionStream(Case.WORST, instructions, 0);
		for(InstructionNode instruction : instructions){
			
			String isUnknown = instruction.getAttributes().getDisplayUnknown() ? "*" : " ";
	     	
	     	String functionOffsetString = 
	     		instruction.getXtaInstruction().getFunctionName() + " + " + instruction.getXtaInstruction().getFunctionOffset();
	     	
	     	m_console.display(String.format("%s %.1f: (%.1fns) 0x%05x %-22s %-12s %s",
	     			isUnknown,
	         		m_currentTime,
	         		instruction.getAttributes().getInstructionTimeTaken(),
	           		instruction.getXtaInstruction().getPc(), 
	           		functionOffsetString, 
	           		instruction.getXtaInstruction().getNmenonic(), 
	           		instruction.getXtaInstruction().getOperandString()));
	     	
	     	if (instruction.getXtaInstruction().canPause())
	     		m_console.display(" (P)");
	     	
	     	m_currentTime += instruction.getAttributes().getInstructionTimeTaken();
	      	m_console.display("\n");    	
		}
		m_console.display("\n");    	
    }
}
