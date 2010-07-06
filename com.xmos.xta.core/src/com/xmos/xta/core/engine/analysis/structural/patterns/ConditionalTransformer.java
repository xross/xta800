package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;

public class ConditionalTransformer extends PatternMatcher {

	public boolean isMatch(CfgNode currentNode, Collection<CfgNode> workingSet, CfgNode treeStart) {
		m_treeStart = treeStart;
		if (currentNode.getNextNodesSize() < 2) {
			return false;
		}
		return explore(currentNode, new NonIteratableSet<CfgNode>(), 
				currentNode.getDominators(), currentNode, workingSet);
	}

	private boolean explore(CfgNode node, NonIteratableSet<CfgNode> seen, 
			Set<CfgNode> dominators, CfgNode start, Collection<CfgNode> workingSet) {
		
		seen.add(node);
		if(node.getDominators().containsAll(dominators) && node.getDominators().size() == dominators.size() + 1){

			if(node.getNextNodesSize() >1 && node.getPrevNodes().size() == 1){
				CommonMethods.wedgeUp(node, workingSet);
				return true;
			}
			if(node.getNextNodesSize() == 1 && node.getPrevNodes().size() > 1){
				CommonMethods.wedgeDown(node, workingSet);
				return true;
			}
		}

		boolean found = false;
		for (CfgNode next : node.getNextNodes()){
			if (!seen.contains(next)){
				found = explore(next, seen, dominators, start, workingSet);
			}
			if (found) {
				return true;
			}
		}
		return false;
	}

	public boolean usesDominatorTree() {
		return true;
	}

}
