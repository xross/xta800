package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SelfLoopNode;

public class SelfLoopPattern extends PatternMatcher {
	public boolean isMatch(Node currentNode, Collection<Node> workingSet,
			boolean verbose, Node treeStart) {
		m_treeStart = treeStart;
		
		if (currentNode.getNextNodes().contains(currentNode)) {
			assert(currentNode.getPrevNodes().contains(currentNode));
			
			SelfLoopNode loop = new SelfLoopNode(currentNode);
			
			if (m_treeStart == currentNode) {
				m_treeStart = loop;
			}
			
			currentNode.removeNextNode(currentNode);
			currentNode.removePrevNode(currentNode);
			
			for (Node next : currentNode.getNextNodes()) {
					next.removePrevNode(currentNode);
					next.addPrevNode(loop);
					loop.addNextNode(next);
			}
			
			for (Node prev : currentNode.getPrevNodes()) {
					prev.removeNextNode(currentNode);
					prev.addNextNode(loop);
					loop.addPrevNode(prev);
			}
			
			assert(workingSet.contains(currentNode));
			workingSet.remove(currentNode);
			workingSet.add(loop);
			
			if (verbose) {
				System.out.println("#found a self loop at " + currentNode);
			}			
			return true;
		} 
		return false;
	}

	public boolean usesDominatorTree() {
		return false;
	}
}
