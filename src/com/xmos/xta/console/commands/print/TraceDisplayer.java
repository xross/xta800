package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status;

public class TraceDisplayer {
	private Console m_console;
	private double m_currentTime;
		
	public TraceDisplayer(Console console) {
		m_console = console;
	}
	
    public void display(Node node, Case c) {
    	if (isInfinite(node, c)) {
    		m_console.display("Can not trace - time is infinite\n");
    	} else {
        	m_currentTime = 0;
    		displayInstructions(node, c, 1);
    		m_console.display("\n");
    	}
    }
    
    private boolean isInfinite(Node node, Case c) {
    	Status status = new Status();
    	node.getAttributes().getTimeTaken(c, status, null);
    	if (status.isInfinite()) {
    		return true;
    	}
		return false;
	}

	private void displayInstructions(Node node, Case c, long iteration) {
		if (node == null || node.isNullNode())
			return;
		
		if (node.isInstructionNode()) {
			displayInstruction((InstructionNode)node);
		} else if (node.isLoopNode() || node.isSelfLoopNode()) {
			if (node.getAttributes().getIsAbsolute()) {
				if (iteration != 1) {
					return; // Only print this once
				}
				
	    		for (int i = 0; i < node.getAttributes().getLoopIterations(); i++) {
	    			displayChildren(node, c, i);
	    		}
				
			} else {
	    		for (int i = 0; i < node.getAttributes().getLoopIterations(); i++) {
	    			displayChildren(node, c, iteration + i);
	    		}
			}
    	} else if (node.isSectionNode()) {
    		Node child = getChild(node, c, iteration);
    		displayInstructions(child, c, iteration);
    	} else {
    		displayChildren(node, c, iteration);
    	}
    }
    
    private Node getChild(Node section, Case c, long iteration) {
    	for (Node child : section.getChildren()) {
    		long childIterations = child.getAttributes().getTotalIterations(c, null);
    		if (iteration <= childIterations) {
    			return child;
    		}
    		iteration -= childIterations;
    	}
    	assert(false);
		return null;
	}

	private void displayChildren(Node node, Case c, long iteration) {
    	for (Node child : node.getChildren()) {
    		displayInstructions(child, c, iteration);
    	}
	}

	private void displayInstruction(InstructionNode instruction) {
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
}

