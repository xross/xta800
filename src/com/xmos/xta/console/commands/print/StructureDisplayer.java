package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class StructureDisplayer {

	private final static boolean DEBUG_NEXT = true;
	private final static boolean DEBUG_BEFORE = false;

	private Console m_console;
	private int m_verbosity;

	public StructureDisplayer(Console console, int verbosity) {
		m_console = console;
		m_verbosity = verbosity;
	}
	
	public void display(Collection<Node> nodes) {
		for (Node node : nodes) {					
		    node.traverse(0, new NodeTraverser() {
				public boolean foundBlockNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG_NEXT) displayWhatsNext(node);
					if (DEBUG_BEFORE) displayWhatsBefore(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundFunctionNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					displayFunctionName(node);
					if (DEBUG_NEXT) displayWhatsNext(node);
					if (DEBUG_BEFORE) displayWhatsBefore(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundLoopNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG_NEXT) displayWhatsNext(node);
					if (DEBUG_BEFORE) displayWhatsBefore(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundPathNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG_NEXT) displayWhatsNext(node);
					if (DEBUG_BEFORE) displayWhatsBefore(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundSectionNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG_NEXT) displayWhatsNext(node);
					if (DEBUG_BEFORE) displayWhatsBefore(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}

				public boolean foundTerminalNode(Node node, int depth) {
					printIndent(depth);
					printModifiers(node, depth);
					displayNodeName(node);
					if (DEBUG_NEXT) displayWhatsNext(node);
					if (DEBUG_BEFORE) displayWhatsBefore(node);
					displayTimeTaken(node);
					m_console.display("\n");
					return true;
				}		    	

				public boolean foundInstructionNode(Node node, int depth) {
					if (m_verbosity > 0) {
						printIndent(depth);
						printModifiers(node, depth);
						displayNodeName(node);
						displayTimeTaken(node);
						displayInstructionTrace(node);
						if (DEBUG_NEXT) displayWhatsNext(node);
						if (DEBUG_BEFORE) displayWhatsBefore(node);
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
			if (node.getAttributes().getDisplayUnknown()) {
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
		m_console.display(node.getAttributes().getTimingSummary()); 		
	}	

	private void displayWhatsNext(Node node) {
		if (node.getParent()!= null && node.getParent().whatsNext(node) != null){
			m_console.display(" ---after: ");
			m_console.display(node.getParent().whatsNext(node).toString());
			m_console.display("---");
		}		
	}
	
	private void displayWhatsBefore(Node node) {
		if (node.getParent()!= null && node.getParent().whatsBefore(node) != null){
			m_console.display(" ---before: ");
			m_console.display(node.getParent().whatsBefore(node).toString());
			m_console.display("---");
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
