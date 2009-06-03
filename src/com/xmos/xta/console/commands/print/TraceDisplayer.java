package com.xmos.xta.console.commands.print;

import java.util.Collection;
import java.util.List;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
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
		m_currentTime = 0;
	}
	
    public void display() {
    	StructureGraph structureGraph = m_engine.getRoute(m_routeId).getStructureGraph();
		Collection<Node> nodes = structureGraph.getThreads();	
		for (Node node : nodes) {					
		    node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, int depth) {
					displayInstruction(node);
					return true;
				}
		    });
		}				
    }
    
    private void displayInstruction(Node node) {
		List<XtaInstruction> instructions = node.getInstructions();
		assert(instructions.size() == 1);
		XtaInstruction instruction = instructions.get(0);
		
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
