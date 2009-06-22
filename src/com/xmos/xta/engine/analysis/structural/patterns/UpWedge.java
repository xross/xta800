package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.TreeSet;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class UpWedge extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		m_treeStart = treeStart;
		
		//the set of least dominant nodes is the set of nodes that cause a loop to be formed.
		Set<Node> leastDominantNodes = new TreeSet<Node>();
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
