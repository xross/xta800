package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

public class StructureDisplayer {
	private final static boolean DEBUG_NEXT = true;
	private final static boolean DEBUG_BEFORE = false;
	private final static boolean DEBUG_CHILDREN = false;
	
	private final static boolean DEBUG_ATTRIBUTES = false;

	private Console m_console;
	private int m_verbosity;

	public StructureDisplayer(Console console, int verbosity) {
		m_console = console;
		m_verbosity = verbosity;
	}
	
	public void display(Collection<Node> nodes) {
		for (Node node : nodes) {					
		    node.traverse(0, new UntypedNodeTraverser() {
				public boolean foundNode(Node node, int depth) {
					displayNodeInfo(node, depth);
					return true;						
				}
		    });
		    m_console.display("\n"); 
		}
    }
	
	private void displayNodeInfo(Node node, int depth) {
		if (node.isInstructionNode() && (m_verbosity <= 0))
			return;
		if (node.isNullNode() && (m_verbosity <= 50))
			return;
		printIndent(depth);
		printModifiers(node, depth);
		displayNodeName(node);
		displayTimeTaken(node);
		displayNumPaths(node);
		displayDescription(node);
		displayAttributes(node);
		if (DEBUG_NEXT) displayWhatsNext(node);
		if (DEBUG_BEFORE) displayWhatsBefore(node);
		if (DEBUG_CHILDREN) displayChildren(node);
		m_console.display("\n");		
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
		String nodeName = String.format("%s(%d)", node.getTypeName(), node.getId());
		m_console.display(String.format("%-14s", nodeName));		
	}
	
	private void displayTimeTaken(Node node) {
		m_console.display(node.getAttributes().getTimingSummary()); 		
	}	

	private void displayNumPaths(Node node) {
		if (m_verbosity >= 10)
		    m_console.display(String.format(" [%d]", 
		    		node.getAttributes().getNumPaths())); 		
	}	
	
	private void displayDescription(Node node) {
		m_console.display(" " + node.getDescription());
	}
	
	private void displayAttributes(Node node) {		
		if ((m_verbosity >= 50) || (DEBUG_ATTRIBUTES == true)) {			
			NodeAttributes nodeAttributes = node.getAttributes();
			
			if (node.isLoopNode()) {
				m_console.display(String.format(" [%d]",
						nodeAttributes.getLoopIterations()));
				
				if (nodeAttributes.getIsAbsolute())
					m_console.display(" (ABSOLUTE)"); 				
				
			} else if (node.isSectionNode()) {
				Node enclosingLoopNode = node.getEnclosingLoopNode();
				if (enclosingLoopNode != null) {
	                for (Node option : node.getChildren()) {
	                	String optionSet = nodeAttributes.getSetOption(option) ? "T" : "F";
	                	m_console.display(String.format("[%s,%d,%d]", optionSet, 
	                			nodeAttributes.getLoopPathIterations(enclosingLoopNode, option, Case.WORST),
	                			nodeAttributes.getLoopPathIterations(enclosingLoopNode, option, Case.BEST)));
	                }					
				}
				if (node.isSectionOptional())		
				    m_console.display(" (OPTIONAL)"); 				
				
			} else if (node.getAttributes().getIsLoopHeader())  {
				m_console.display(" (HEADER)"); 
			}			
		}
	}
	
	private void displayWhatsNext(Node node) {
		if (node.getParent()!= null && node.getParent().whatsNext(node) != null){
			m_console.display(" ---next: ");
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
	
	private void displayChildren(Node node) {
		if (node.getChildren() != null){
			m_console.display(" ---children: ");
			m_console.display(node.getChildren().toString());
			m_console.display("---");
		}		
	}
}
