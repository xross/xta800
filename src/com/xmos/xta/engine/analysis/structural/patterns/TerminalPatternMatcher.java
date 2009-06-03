package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.TerminalNode;

public class TerminalPatternMatcher extends PatternMatcher {
	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		m_treeStart = treeStart;
		
		if(currentNode.getNextNodes().size() < 2){
			return false;
		}
		
		//if every next node is a terminal then can terminate		
		for (Node next : currentNode.getNextNodes()) {
			if (!(next.getNextNodes().size() == 0 && next.getPrevNodes().size() == 1)) {
				return false;
			}
		}
		
		if (verbose) {
			System.out.println("#found a terminal node from " + currentNode + " of " + currentNode.getNextNodes());
		}
		
		TerminalNode newNode = new TerminalNode(currentNode.getNextNodes());
		workingSet.removeAll(currentNode.getNextNodes());
		workingSet.add(newNode);
		currentNode.getNextNodes().clear();
		
		CommonMethods.connect(newNode, currentNode);		
		return true;	
	}
	
	public boolean usesDominatorTree() {
		return false;
	}
}
