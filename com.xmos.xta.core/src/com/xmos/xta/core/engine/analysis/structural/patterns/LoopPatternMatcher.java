package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.InfiniteLoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.LoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class LoopPatternMatcher extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart, Aborter aborter ) throws AbortedException {
		// a loop pattern is when one or more nodes are doubly attached to a
		// single node
		Set<Node> intersection = new IteratableSet<Node>(currentNode.getNextNodes());
		intersection.retainAll(currentNode.getPrevNodes());
		if (intersection.size() > 0) {
			Set <Node> workingIntersection = new IteratableSet<Node>();

			for (Node loop : intersection) {
				if (!(loop.getNextNodesSize() != 1 || !loop.getNextNodes().contains(currentNode) || loop.getPrevNodesSize() != 1)) {
					workingIntersection.add(loop);
				}
			}
			if (workingIntersection.size() > 0 && !intersection.contains(treeStart)) {
				updateGraph(workingIntersection, workingSet, currentNode, treeStart);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	protected void updateGraph(Set<Node> intersection, Collection<Node> workingSet,
			Node header, Node treeStart) {
		Node loop = null;

		boolean noNextNode = false;

		if (header.getNextNodesSize() == intersection.size()){
			noNextNode = true;
		}

		if (noNextNode){
			loop = new InfiniteLoopNode(intersection, header);
		} else {
			loop = new LoopNode(intersection, header);
		}

		if (intersection.contains(treeStart) || header == treeStart){
			m_treeStart = loop;
		} else {
			m_treeStart = treeStart;
		}

		for (Node prev : header.getPrevNodes()) {
			if (!intersection.contains(prev)) {
				loop.addPrevNode(prev);
				prev.removeNextNode(header);
				//prev.getNextNodes().remove(base);
				prev.addNextNode(loop);
			}
		}

		for (Node next : header.getNextNodes()) {
			if (!intersection.contains(next)) {
				loop.addNextNode(next);

				next.removePrevNode(header);
				//next.getPrevNodes().remove(base);
				next.addPrevNode(loop);
			}
		}

		if (noNextNode){
			CommonMethods.connect(CommonMethods.findNullNode(workingSet), loop);
		}

		workingSet.removeAll(intersection);
		workingSet.remove(header);
		workingSet.add(loop);
	}


	public boolean usesDominatorTree() {
		return false;
	}
}
