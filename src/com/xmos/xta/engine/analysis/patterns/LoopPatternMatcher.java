package com.xmos.xta.engine.analysis.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.xmos.xta.engine.analysis.nodes.LoopNode;
import com.xmos.xta.engine.analysis.nodes.Node;

public class LoopPatternMatcher extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		// a loop pattern is when one or more nodes are doubly attached to a
		// single node

		Set<Node> intersection = new HashSet<Node>(currentNode.getNextNodes());
		intersection.retainAll(currentNode.getPrevNodes());
		if (intersection.size() > 0) {
			Set <Node> workingIntersection = new HashSet<Node>();
			
			for (Node loop : intersection) {
				if (!(loop.getNextNodes().size() != 1 || !loop.getNextNodes().contains(currentNode) || loop.getPrevNodes().size() != 1)) {
					workingIntersection.add(loop);
				}
			}
			if (workingIntersection.size() > 0) {
				updateGraph(workingIntersection, workingSet, currentNode, verbose, treeStart);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	protected void updateGraph(Set<Node> intersection, Collection<Node> workingSet, 
			Node base, boolean verbose, Node treeStart) {		

		LoopNode loop = new LoopNode(intersection, base);
		
		if(intersection.contains(treeStart) || base == treeStart){
			m_treeStart = loop;
		} else {
			m_treeStart = treeStart;
		}
					
		if (verbose){
			System.out.println("#have made a new loop with " + base + " as the loop header and " + intersection + " as the intersection and new node is " +loop);
		}
		
		for (Node prev : base.getPrevNodes()) {
			if (!intersection.contains(prev)) {
				loop.addPrevNode(prev);
				prev.getNextNodes().remove(base);
				prev.addNextNode(loop);
			}
		}
		for (Node next : base.getNextNodes()) {
			if (!intersection.contains(next)) {
				loop.addNextNode(next);
				next.getPrevNodes().remove(base);
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
