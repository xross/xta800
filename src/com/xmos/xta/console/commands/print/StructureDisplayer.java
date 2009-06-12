package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class StructureDisplayer {
	private final static boolean DEBUG = true;

	private Console m_console;

	public StructureDisplayer(Console console) {
		m_console = console;
	}
	
	public void display(Collection<Node> nodes) {
		for (Node node : nodes) {					
		    node.traverse(0, new NodeTraverser() {
				public boolean foundBlockNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundFunctionNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					displayFunctionName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundLoopNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundPathNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundSectionNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundTerminalNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG) displayWhatsNext(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}		    	

				public boolean foundInstructionNode(Node node, int depth) {
					if (DEBUG) {
						printIndent(depth);
						printModifiers(node, depth);
						displayNodeName(node);
						displayTimeTaken(node);
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

	private void printModifiers(Node node, int depth) {
		if (depth > 0) {
			if (node.isExcluded()) {
				m_console.display(".  X ");
			} else if (node.getAttributes().getDisplayUnknown()) {
				m_console.display(".  * ");
			} else {
				m_console.display(".    ");			
			}			
		}
	}	
	
	private void displayNodeName(Node node) {	
		String nodeName = "";
		if (node.getAttributes().isLoopHeader()) {
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
		m_console.display(node.getTimingSummary()); 
		
		if (DEBUG) {
			m_console.display(String.format(" {%d}", 
					node.getAttributes().getNumIterations()));
		}
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
}
