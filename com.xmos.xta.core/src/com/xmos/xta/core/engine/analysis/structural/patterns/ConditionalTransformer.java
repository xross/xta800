package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class ConditionalTransformer extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart) {
		m_treeStart = treeStart;
		if (currentNode.getNextNodesSize() < 2) {
			return false;
		}
		return explore(currentNode, new TreeSet<Node>(), currentNode.getDominators(), currentNode, workingSet);
	}

	private boolean explore(Node node, TreeSet<Node> seen, Set<Node> dominators, Node start, Collection<Node> workingSet) {
		seen.add(node);
		if(node.getDominators().containsAll(dominators) && node.getDominators().size() == dominators.size() + 1){

			if(node.getNextNodesSize() >1 && node.getPrevNodesSize() == 1){
				CommonMethods.wedgeUp(node, workingSet);
				return true;
			}
			if(node.getNextNodesSize() == 1 && node.getPrevNodesSize() > 1){
				CommonMethods.wedgeDown(node, workingSet);
				return true;
			}
		}

		boolean found = false;
		for(Node next : node.getNextNodes()){
			if(!seen.contains(next)){
				found = explore(next, seen, dominators, start, workingSet);
			}
			if(found) return true;
		}
		return false;
	}

	public boolean usesDominatorTree() {
		return true;
	}

}
