package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class UpWedge extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart) {
		m_treeStart = treeStart;

		//the set of least dominant nodes is the set of nodes that cause a loop to be formed.
		Set<Node> leastDominantNodes = new TreeSet<Node>();
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
			while (piviot.getPrevNodesSize() == 1 && piviot.getNextNodesSize() == 1) {
				piviot = piviot.getPrevNode();
			}

			if (piviot.getPrevNodesSize() > 1) {
				return CommonMethods.wedgeDown(piviot, workingSet);
			} else {
				return CommonMethods.wedgeUp(piviot, workingSet);
			}

		}
		return false;
	}

	public boolean usesDominatorTree() {
		return true;
	}
}
