package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.NodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class StructureDisplayer {
	private final static boolean DEBUG = false;

	private Console m_console;

	public StructureDisplayer(Console console) {
		m_console = console;
	}
	
	public void display(Collection<Node> nodes) {
		for (Node node : nodes) {					
		    node.traverse(0, new NodeTraverser() {
				public boolean foundBlockNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("block(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					if (DEBUG) displayWhatsNext(node);
					m_console.display(String.format(" : %.1fns", node.getTimeTaken(Case.WORST)));
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundFunctionNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("function(%d)", node.getId());
					m_console.display(String.format("%-14s (%s)", nodeName, node.getFunctionName()));
					if (DEBUG) displayWhatsNext(node);
					m_console.display(String.format(" : %.1fns", node.getTimeTaken(Case.WORST)));
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundLoopNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("loop(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					if (DEBUG) displayWhatsNext(node);
					m_console.display(String.format(" : %.1fns", node.getTimeTaken(Case.WORST)));
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundPathNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("path(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					if (DEBUG) displayWhatsNext(node);
					m_console.display(String.format(" : %.1fns", node.getTimeTaken(Case.WORST)));
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundSectionNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("section(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					if (DEBUG) displayWhatsNext(node);
					m_console.display(String.format(" : %.1fns", node.getTimeTaken(Case.WORST)));
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundTerminalNode(Node node, int depth) {
					printIndent(depth);
					String nodeName = String.format("terminal(%d)", node.getId());
					m_console.display(String.format("%-14s", nodeName));
					if (DEBUG) displayWhatsNext(node);
					m_console.display(String.format(" : %.1fns", node.getTimeTaken(Case.WORST)));
					displayNumIterations(node);
					m_console.display("\n");
					return true;
				}		    	

				public boolean foundInstructionNode(Node node, int depth) {
					if (DEBUG) {
						printIndent(depth);
						String nodeName = String.format("instruction(%d)", node.getId());
						m_console.display(String.format("%-14s", nodeName));
						m_console.display(String.format(" : %.1fns", node.getTimeTaken(Case.WORST)));						
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
		for (int i = 0; i < depth; ++i) {
			m_console.display(".    ");
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
	
	private void displayNumIterations(Node node) {
		if (isWithinLoop(node)) {
		    m_console.display(String.format(" (x%d)", node.getNumIterations()));
		}
	}
	
	private boolean isWithinLoop(Node node) {		
		return (node.getEnclosingLoopNode() != null);
	}	
}
