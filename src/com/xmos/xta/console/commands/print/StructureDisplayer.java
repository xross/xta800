package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

public class StructureDisplayer {
	private final static boolean DEBUG_NEXT = false;
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
		final StringBuffer sb = new StringBuffer();
		for (Node node : nodes) {					
		    node.traverse(0, new UntypedNodeTraverser() {
				public boolean foundNode(Node node, int depth) {
					displayNodeInfo(node, depth, sb);
					return true;						
				}
		    });
		    sb.append("\n");	
		}
		m_console.display(sb.toString());
    }
	
	private void displayNodeInfo(Node node, int depth, StringBuffer sb) {
		if (node.isInstructionNode() && (m_verbosity <= 0))
			return;
		if (node.isNullNode() && (m_verbosity <= 50))
			return;
		printIndent(depth, sb);
		printModifiers(node, depth, sb);
		displayNodeName(node, sb);
		displayTimeTaken(node, sb);
		displayNumPaths(node, sb);
		displayDescription(node, sb);
		displayAttributes(node, sb);
		if (DEBUG_NEXT) displayWhatsNext(node, sb);
		if (DEBUG_BEFORE) displayWhatsBefore(node, sb);
		if (DEBUG_CHILDREN) displayChildren(node, sb);
		sb.append("\n");	
	}
	
	private void printIndent(int depth, StringBuffer sb) {
		for (int i = 0; i < depth - 1; ++i) {
			sb.append(".    ");
		}
	}

	private void printModifiers(Node node, int depth, StringBuffer sb) {
		if (depth > 0) {
			if (node.getAttributes().getDisplayUnknown()) {
				sb.append(".  * ");
			} else {
				sb.append(".    ");		
			}			
		}
	}	
	
	private void displayNodeName(Node node, StringBuffer sb) {	
		String nodeName = String.format("%s(%d)", node.getTypeName(), node.getId());
		sb.append(String.format("%-14s", nodeName));		
	}
	
	private void displayTimeTaken(Node node, StringBuffer sb) {
		sb.append(node.getAttributes().getTimingSummary());	
	}	

	private void displayNumPaths(Node node, StringBuffer sb) {
		if (m_verbosity >= 10)
			sb.append(String.format(" [%d]", 
		    		node.getAttributes().getNumPaths())); 		
	}	
	
	private void displayDescription(Node node, StringBuffer sb) {
		sb.append(" " + node.getDescription());
	}
	
	private void displayAttributes(Node node, StringBuffer sb) {		
		if ((m_verbosity >= 50) || (DEBUG_ATTRIBUTES == true)) {			
			NodeAttributes nodeAttributes = node.getAttributes();
			
			if (node.isUnresolved())
				sb.append(" UNRESOLVED");
			
			if (node.isLoopNode() || node.isSelfLoopNode()) {
				sb.append(String.format(" [%d]",
						nodeAttributes.getLoopIterations()));
				
				if (nodeAttributes.getIsAbsolute())
					sb.append(" (ABSOLUTE)"); 				
				
			} else if (node.isSectionNode()) {
				Node enclosingLoopNode = node.getEnclosingLoopNode();
				if (enclosingLoopNode != null) {
	                for (Node option : node.getChildren()) {
	                	String optionSet = nodeAttributes.getSetOption(option) ? "T" : "F";
	                	sb.append(String.format("[%s,%d,%d]", optionSet, 
	                			nodeAttributes.getLoopPathIterations(
	                					enclosingLoopNode.getAttributes().getLoopIterations(), option, Case.WORST),
	                			nodeAttributes.getLoopPathIterations(
	                					enclosingLoopNode.getAttributes().getLoopIterations(), option, Case.BEST)));
	                }					
				}
				if (node.isSectionOptional())		
					sb.append(" (OPTIONAL)"); 				
				
			} else if (node.getAttributes().getIsLoopHeader())  {
				sb.append(" (HEADER)"); 
			}			
		}
	}
	
	private void displayWhatsNext(Node node, StringBuffer sb) {
		if (node.getParent()!= null && node.getParent().whatsNext(node) != null){
			sb.append(" ---next: ");
			sb.append(node.getParent().whatsNext(node).toString());
			sb.append("---");
		}		
	}
	
	private void displayWhatsBefore(Node node, StringBuffer sb) {
		if (node.getParent()!= null && node.getParent().whatsBefore(node) != null){
			sb.append(" ---before: ");
			sb.append(node.getParent().whatsBefore(node).toString());
			sb.append("---");
		}		
	}
	
	private void displayChildren(Node node, StringBuffer sb) {
		if (node.getChildren() != null){
			sb.append(" ---children: ");
			sb.append(node.getChildren().toString());
			sb.append("---");
		}		
	}
}
