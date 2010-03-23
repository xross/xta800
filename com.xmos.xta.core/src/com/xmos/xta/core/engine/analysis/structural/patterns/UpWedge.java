package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class UpWedge extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart, Aborter aborter ) throws AbortedException {
		m_treeStart = treeStart;

		//the set of least dominant nodes is the set of nodes that cause a loop to be formed.
		Set<Node> leastDominantNodes = new NonIteratableSet<Node>();
		for (Node node : workingSet) {
			for (Node next : node.getNextNodes()) {
				if (node.dominatorsContain(next)) {
					leastDominantNodes.add(node);
				}
			}
		}

		if (leastDominantNodes.isEmpty()) {
			return false;
		}
		if (leastDominantNodes.contains(currentNode)) {
			Node piviot = currentNode;
			while (piviot.getPrevNodes().size() == 1 && piviot.getNextNodes().size() == 1) {
				piviot = piviot.getPrevNode();
			}

			if (piviot.getPrevNodes().size() > 1) {
				return CommonMethods.wedgeDown(piviot, workingSet, aborter);
			} else {
				return CommonMethods.wedgeUp(piviot, workingSet, aborter);
			}

		}
		return false;
	}

	public boolean usesDominatorTree() {
		return true;
	}
}
