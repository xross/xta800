package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.TreeSet;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.LoopNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class LoopPatternMatcher extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart) {
		// a loop pattern is when one or more nodes are doubly attached to a
		// single node

		Set<Node> intersection = new TreeSet<Node>(currentNode.getNextNodes());
		intersection.retainAll(currentNode.getPrevNodes());
		if (intersection.size() > 0) {
			Set <Node> workingIntersection = new TreeSet<Node>();
			
			for (Node loop : intersection) {
				if (!(loop.getNextNodesSize() != 1 || !loop.getNextNodes().contains(currentNode) || loop.getPrevNodesSize() != 1)) {
					workingIntersection.add(loop);
				}
			}
			if (workingIntersection.size() > 0) {
				updateGraph(workingIntersection, workingSet, currentNode, treeStart);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	protected void updateGraph(Set<Node> intersection, Collection<Node> workingSet, 
			Node base, Node treeStart) {		

		LoopNode loop = new LoopNode(intersection, base);
		
		if (intersection.contains(treeStart) || base == treeStart){
			m_treeStart = loop;
		} else {
			m_treeStart = treeStart;
		}
		
		for (Node prev : base.getPrevNodes()) {
			if (!intersection.contains(prev)) {
				loop.addPrevNode(prev);
				prev.removeNextNode(base);
				//prev.getNextNodes().remove(base);
				prev.addNextNode(loop);
			}
		}
		for (Node next : base.getNextNodes()) {
			if (!intersection.contains(next)) {
				loop.addNextNode(next);
				
				next.removePrevNode(base);
				//next.getPrevNodes().remove(base);
				next.addPrevNode(loop);
			}
		}
		workingSet.removeAll(intersection);
		workingSet.remove(base);
		workingSet.add(loop);
	}

	public boolean usesDominatorTree() {
		return false;
	}
}
