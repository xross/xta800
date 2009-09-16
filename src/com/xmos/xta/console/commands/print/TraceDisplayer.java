package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.LoopNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.engine.instructions.XtaInstruction;

@SuppressWarnings("serial")
class EscapedDetctedException extends Exception {}

public class TraceDisplayer {
	private Console m_console;
	private double m_currentTime;
		
	public TraceDisplayer(Console console) {
		m_console = console;
	}
	
    public void display(Node node, Case c) {
    	if (isInfinite(node, c)) {
    		m_console.display("Cannot trace - time is infinite\n");
    	} else {
        	m_currentTime = 0;        	        
    		try {
				displayInstructions(node, c, 1, 0);
			} catch (EscapedDetctedException e) {
				m_console.display("Trace aborted...\n");
			}
    		m_console.display("\n");
    	}
    }
    
    private boolean isInfinite(Node node, Case c) {
    	// Allow the user to always trace blocks
    	if (node.isBlockNode())
    		return false;
    	
    	Status status = new Status();
    	node.getAttributes().getTimeTaken(c, status, null);
    	if (status.isInfinite()) {
    		return true;
    	}
		return false;
	}

	private void displayInstructions(Node node, Case c, long totalIterations, long iteration) throws EscapedDetctedException {
		if (node == null || node.isNullNode())
			return;
		
		if (node.isInstructionNode()) {
			displayInstruction((InstructionNode)node);
			
		} else if (node.isLoopNode() || node.isSelfLoopNode()) {
			displayLoop(node, c, totalIterations, iteration);
			
    	} else if (node.isCondNode()) {
    		Node child = getChild(node, c, totalIterations, iteration);
    		displayInstructions(child, c, totalIterations, iteration);
    		
    	} else {
    		displayChildren(node, c, totalIterations, iteration);
    	}
    }
    
	private Node getChild(Node cond, Case c, long totalIterations, long iteration) {
    	for (Node child : cond.getChildren()) {
    		long childIterations = cond.getAttributes().getLoopPathIterations(totalIterations, child, c);
    		if (iteration < childIterations) {
    			return child;
    		}
    		iteration -= childIterations;
    	}
		return null;
	}

	private void displayChildren(Node node, Case c, long totalIterations, long iteration) throws EscapedDetctedException {
    	for (Node child : node.getChildren()) {
    		displayInstructions(child, c, totalIterations, iteration);
    	}
	}

    private void displayLoop(Node node, Case c, long totalIterations, long iteration) throws EscapedDetctedException {
		long loopIterations = node.getAttributes().getLoopIterations();
		if (node.getAttributes().getIsAbsolute()) {
			if (iteration == 0) {
				// Only print body of absolute once
	    		for (int i = 0; i < loopIterations; i++) {
	    			displayChildren(node, c, loopIterations, i);
	    		}
			}

			if (node.isLoopNode()) {
				displayInstructions(((LoopNode)node).getLoopHeader(), c, totalIterations, iteration);
			}
			
		} else {
    		for (int i = 0; i < loopIterations; i++) {
    			displayChildren(node, c, loopIterations, i);
    		}
			if (node.isLoopNode()) {
				displayInstructions(((LoopNode)node).getLoopHeader(), c, totalIterations, 0);
			}
		}
	}
    
	private void displayInstruction(InstructionNode instruction) throws EscapedDetctedException {
		String isUnknown = instruction.getAttributes().getDisplayUnknown() ? "*" : " ";
		String isUnresolved = instruction.isUnresolved() ? " [UNRESOLVED]" : "";
     	
     	XtaInstruction xtaInstruction = instruction.getXtaInstruction();
		String functionName = xtaInstruction.getFunctionName();
		
		String functionOffsetString = "";
		if (functionName != null) { 
			functionOffsetString = functionName + " + " + xtaInstruction.getFunctionOffset();
		}
     	
     	m_console.display(String.format("%s %.1f: (%.1fns) 0x%05x %-22s %-12s %s%s",
     			isUnknown,
         		m_currentTime,
         		instruction.getAttributes().getInstructionTimeTaken(),
           		xtaInstruction.getPc(), 
           		functionOffsetString, 
           		xtaInstruction.getNmenonic(), 
           		xtaInstruction.getOperandString(),
           		isUnresolved));
     	
     	if (xtaInstruction.canPause())
     		m_console.display(" (P)");
     	
     	m_currentTime += instruction.getAttributes().getInstructionTimeTaken();
      	m_console.display("\n");    
      	
      	if (m_console.hasEscaped()) {
      		throw new EscapedDetctedException();
      	}
    }
}

