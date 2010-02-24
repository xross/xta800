package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class NullRemover  extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart) {
		m_treeStart = treeStart;
		
		if(!currentNode.isNullNode()){
			return false;
		}
		
		assert(currentNode.getNextNodes().size() < 2);
		
		for(Node next : currentNode.getNextNodes()){
			if(next.isNullNode()){
				workingSet.remove(currentNode);
				for(Node prev : currentNode.getPrevNodes()){
					prev.removeNextNode(currentNode);
					prev.addNextNode(next);
					next.addPrevNode(prev);
				}
				next.removePrevNode(currentNode);
				return true;
			}
		}
		return false;
	}


	public boolean usesDominatorTree() {
		return false;
	}

}