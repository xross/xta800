package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class StructureDisplayer {

	private final static boolean DEBUG = false;
	private final static boolean DEBUG_LOOP_ITERATIONS = false;

	private Console m_console;

	public StructureDisplayer(Console console) {
		m_console = console;
	}
	
	public void display(Collection<Node> nodes) {
		for (Node node : nodes) {					
		    node.traverse(0, new NodeTraverser() {
				public boolean foundBlockNode(Node node, int depth) {
					printIndent(depth);
					printUnknown(node);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundFunctionNode(Node node, int depth) {
					printIndent(depth);
					printUnknown(node);
					displayNodeName(node);
					displayFunctionName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundLoopNode(Node node, int depth) {
					printIndent(depth);
					printUnknown(node);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundPathNode(Node node, int depth) {
					printIndent(depth);
					printUnknown(node);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundSectionNode(Node node, int depth) {
					printIndent(depth);
					printUnknown(node);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundTerminalNode(Node node, int depth) {
					printIndent(depth);
					printUnknown(node);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}		    	

				public boolean foundInstructionNode(Node node, int depth) {
					if (DEBUG) {
						printIndent(depth);
						printUnknown(node);
						displayNodeName(node);
						displayTimeTaken(node);
						displayNumIterations(node);
						displayInstructionTrace(node);
						displayWhatsNext(node);
						m_console.display("\n");
					}
					return true;						
				}
		    });
		    m_console.display("\n"); 
		}		
		
    }
	
	private void printIndent(int depth) {
		for (int i = 0; i < depth - 1; ++i) {
			m_console.display(".    ");
		}
	}	

	private void printUnknown(Node node) {
		if (node.getNodeAttributes().getDisplayUnknown()) {
			m_console.display(".  * ");
		} else {
			m_console.display(".    ");			
		}
	}	
	
	private void displayNodeName(Node node) {	
		String nodeName = "";
		if (node.isLoopHeader()) {
			nodeName = String.format("header(%d)", node.getId());			
		} else {
			nodeName = String.format("%s(%d)", node.getTypeName(), node.getId());
		}
		m_console.display(String.format("%-14s", nodeName));		
	}

	private void displayFunctionName(Node node) {
		m_console.display(String.format("[%s]", node.getFunctionName()));
	}
	
	private void displayTimeTaken(Node node) {
		m_console.display(String.format(" : %.1fns", 
				node.getTimeTaken(Case.WORST)));					
        displayLoopIterationCalculation(node);        
	}	

	private void displayWhatsNext(Node node) {
		if (node.getParent()!= null && node.getParent().whatsNext(node) != null){
			m_console.display(" ");
			m_console.display(node.getParent().whatsNext(node).toString());
		}		
	}
	
	private void displayInstructionTrace(Node node) {
		XtaInstruction instruction = node.getInstructions().getFirst();
		m_console.display(String.format(" 0x%x: %s %s", 
				instruction.getPc(),
				instruction.getNmenonic(),
				instruction.getOperandString()));
	}
	
	private void displayNumIterations(Node node) {
		Node parent = node.getParent();
		if ((parent != null) && parent.isSectionNode()) {
    	    m_console.display(String.format(" (%d iterations)", node.getNumIterations()));
		}
	}
	
	private void displayLoopIterationCalculation(Node node) {
		// Only display these calculations on the children of a loop node..
		Node parent = node.getParent();
		if ((parent != null) && parent.isLoopNode()) {	
			double relativeTimeTaken = node.getTimeTakenRelative(Case.WORST);
			double absoluteTimeTaken = node.getTimeTakenAbsolute(Case.WORST);
			
			// Displays a more complete calculation for the case when absolutes are involved.
			if ((absoluteTimeTaken != 0) && !parent.getIsAbsolute()) {								   								
			    m_console.display(String.format(" ((%.1fns x %d) + %.1fns)",
			    		relativeTimeTaken, 
			    		node.getNumIterations(),
			    		absoluteTimeTaken));				
			} else {
			    m_console.display(String.format(" x %d", node.getNumIterations()));
			}
		}
		
		if (DEBUG_LOOP_ITERATIONS) {
		    m_console.display(String.format(" {%.1fns/%.1fns}", 
			    	node.getTimeTakenRelative(Case.WORST),
				    node.getTimeTakenAbsolute(Case.WORST)));
		    
		    if (node.getIsAbsolute()) {
		    	m_console.display(" (ABS)");
		    } else {
		    	m_console.display(" (REL)");		    	
		    }
		}			
	}
}
