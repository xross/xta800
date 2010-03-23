package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class PanicTransformer extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart, Aborter aborter ) throws AbortedException {
		m_treeStart = treeStart;
		if (currentNode.getNextNodesSize() < 2) {
			return false;
		}
		return explore(currentNode, new NonIteratableSet<Node>(), currentNode.getDominators(), currentNode, workingSet, aborter);
	}

	private boolean explore(Node node, NonIteratableSet<Node> seen, Set<Node> dominators, Node start, Collection<Node> workingSet, Aborter aborter) throws AbortedException {
		seen.add(node);
		if(node.getDominators().containsAll(dominators) && node.getDominators().size() == dominators.size() + 1){
			if(node.getNextNodesSize() >1 && node.getPrevNodes().size() > 1){
				CommonMethods.wedgeDown(node, workingSet, aborter);
				return true;
			}
		}
		boolean found = false;
		for(Node next : node.getNextNodes()){
			if(!seen.contains(next)){
				found = explore(next, seen, dominators, start, workingSet, aborter);
			}
			if(found) return true;
		}
		return false;
	}

	public boolean usesDominatorTree() {
		return true;
	}

}
