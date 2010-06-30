package com.xmos.xta.core.console.commands.print;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.LoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;

@SuppressWarnings("serial")
class EscapedDetctedException extends Exception {}
@SuppressWarnings("serial")
class UnresolvedDetctedException extends Exception {}
@SuppressWarnings("serial")
class RecursionDetctedException extends Exception {}
@SuppressWarnings("serial")
class IllegalException extends Exception {}

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
			} catch (UnresolvedDetctedException e) {

			} catch (RecursionDetctedException e) {
			} catch (IllegalException e) {}			
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

	private void displayInstructions(Node node, Case c, long totalIterations, long iteration) throws EscapedDetctedException, 
	RecursionDetctedException, UnresolvedDetctedException, IllegalException {

		if (node == null || node.isNullNode())
			return;

		if (node.isInstructionNode()) {
			displayInstruction((InstructionNode)node);

		} else if (node.isLoopNode() || node.isSelfLoopNode()) {
			displayLoop(node, c, totalIterations, iteration);

    	} else if (node.isCondNode()) {
    		Node child = getChild(node, c, totalIterations, iteration);
    		displayInstructions(child, c, totalIterations, iteration);

    	} else if (node.getAttributes().getFixedTime() != null) {
    		// Deal with function/sequence nodes which have a fixed time
    		double timeBefore = m_currentTime;
    		displayChildren(node, c, totalIterations, iteration);
    		double timeAfter = m_currentTime;

    		double fixedTime = node.getAttributes().getFixedTime().getNs();
    		double timeDifference = fixedTime - (timeAfter - timeBefore);
         	m_console.display(String.format("  %.1f: (%.1fns) fixed %s time\n",
             		m_currentTime,
             		timeDifference,
             		node.isFunctionNode() ? "function" : "path"));

         	m_currentTime = m_currentTime + timeDifference;

    	} else {
    		displayChildren(node, c, totalIterations, iteration);
    	}
    }

	private Node getChild(Node cond, Case c, long totalIterations, long iteration) {
    	for (Node child : cond.iChildren()) {
    		long childIterations = cond.getAttributes().getLoopPathIterations(totalIterations, child, c);
    		if (iteration < childIterations) {
    			return child;
    		}
    		iteration -= childIterations;
    	}
		return null;
	}

	private void displayChildren(Node node, Case c, long totalIterations, long iteration) throws EscapedDetctedException, RecursionDetctedException, UnresolvedDetctedException, IllegalException {
    	for (Node child : node.iChildren()) {
    		displayInstructions(child, c, totalIterations, iteration);
    	}
	}

    private void displayLoop(Node node, Case c, long totalIterations, long iteration) throws EscapedDetctedException, IllegalException, RecursionDetctedException, UnresolvedDetctedException {
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

	private void displayInstruction(InstructionNode instruction) throws EscapedDetctedException, 
	IllegalException, UnresolvedDetctedException, RecursionDetctedException {
		String isUnknown = instruction.getAttributes().getDisplayAsUnknown() ? "*" : " ";
		String isUnresolved = instruction.isUnresolved() ? " [UNRESOLVED]" : "";
		String formsRecursion = instruction.getFormsRecursion() ? " [RECURSION]" : "";

     	XtaInstruction xtaInstruction = instruction.getXtaInstruction();
		String functionName = xtaInstruction.getFunctionName();		
		String isIllegal = xtaInstruction.getIsIllegal() ? " [ILLEGAL]" : "";

		String functionOffsetString = "";
		if (functionName != null) {
			functionOffsetString = functionName + " + " + xtaInstruction.getFunctionOffset();
		}

     	m_console.display(String.format("%s %.1f: (%.1fns) 0x%05x %-22s %-12s %s%s%s%s",
     			isUnknown,
         		m_currentTime,
         		instruction.getAttributes().getFixedTime().getNs(),
           		xtaInstruction.getPc(),
           		functionOffsetString,
           		xtaInstruction.getMnemonic(),
           		xtaInstruction.getOperandString(),
           		isUnresolved, 
           		formsRecursion,
           		isIllegal));

     	if (xtaInstruction.canPause())
     		m_console.display(" (P)");

     	m_currentTime += instruction.getAttributes().getFixedTime().getNs();
      	m_console.display("\n");

      	if(instruction.getFormsRecursion()){
      		throw new RecursionDetctedException();
      	}
      	if(instruction.isUnresolved()){
      		throw new UnresolvedDetctedException();
      	}
      	if (m_console.hasEscaped()) {
      		throw new EscapedDetctedException();
      	}
      	if (xtaInstruction.getIsIllegal()) {
      	    throw new IllegalException();	
      	}      		
    }
}

