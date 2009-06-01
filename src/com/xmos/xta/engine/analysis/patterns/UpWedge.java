package com.xmos.xta.engine.analysis.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.xmos.xta.engine.analysis.nodes.Node;

public class UpWedge extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		m_treeStart = treeStart;
		
		Set<Node> leastDominantNodes = new HashSet<Node>();
		for (Node node : workingSet) {
			for (Node next : node.getNextNodes()) {
				if (node.getDominators().contains(next)) {
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
				piviot = piviot.getPrevNodes().iterator().next();
			}
			
			if (piviot.getPrevNodes().size() > 1) {
				return false;
			}
			return CommonMethods.wedgeUp(piviot, workingSet, verbose);
		}		
		return false;
	}

	public boolean usesDominatorTree() {
		return true;
	}
}
