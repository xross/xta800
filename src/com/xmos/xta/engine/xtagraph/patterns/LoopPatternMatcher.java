package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.xmos.xta.engine.xtagraph.nodes.Loop;
import com.xmos.xta.engine.xtagraph.nodes.Node;

public class LoopPatternMatcher extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose) {

		// a loop pattern is when one or more nodes are doubly attached to a
		// single node

		Set<Node> intersection = new HashSet<Node>(currentNode.getNextNodes());
		intersection.retainAll(currentNode.getPrevNodes());
		if (intersection.size() > 0) {
			Set <Node> workingIntersection = new HashSet<Node>();
			
			for (Node loop : intersection) {
				if (loop.getNextNodes().size() != 1 || !loop.getNextNodes().contains(currentNode) || loop.getPrevNodes().size() != 1) {
					//allLoopsResolved = false;
				} else {
					workingIntersection.add(loop);
				}
			}
			if (workingIntersection.size() > 0) {
				updateGraph(workingIntersection, workingSet, currentNode, verbose);
				return true;
			} else {
				return false;
			}

		}
		return false;

	}

	protected void updateGraph(Set<Node> intersection, Collection<Node> workingSet, Node base, boolean verbose) {
		
		Loop loop = new Loop(intersection, base);
		
		if (verbose){
			System.out.println("#have made a new loop with " + base + " as the loop header and " + intersection + " as the intersection and new node is " +loop);
		}

		
		for (Node prev : base.getPrevNodes()) {
			if(!intersection.contains(prev)){
				loop.addPrevNode(prev);
				prev.getNextNodes().remove(base);
				prev.addNextNode(loop);
			}
		}
		for (Node next : base.getNextNodes()) {
			if(!intersection.contains(next)){
				loop.addNextNode(next);
				next.getPrevNodes().remove(base);
				next.addPrevNode(loop);
			}
		}
		workingSet.removeAll(intersection);
		workingSet.remove(base);
		workingSet.add(loop);
	}
}
