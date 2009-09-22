package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.xta.core.engine.analysis.structural.nodes.InfiniteSelfLoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.SelfLoopNode;

public class SelfLoopPattern extends PatternMatcher {
	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart) {
		m_treeStart = treeStart;
		
		if (currentNode.getNextNodes().contains(currentNode)) {
			assert(currentNode.getPrevNodes().contains(currentNode));
			Node loop = null;
			
			if(currentNode.getNextNodesSize() == 1){
				loop = new InfiniteSelfLoopNode(currentNode);
				
				Node nullNode = CommonMethods.findNullNode(workingSet);
				if(nullNode != null){
					CommonMethods.connect(nullNode, loop);
				}
				
			} else {
				loop = new SelfLoopNode(currentNode);
			}
			
			if (m_treeStart == currentNode) {
				m_treeStart = loop;
			}
			
			currentNode.removeNextNode(currentNode);
			currentNode.removePrevNode(currentNode);
			
			for (Node next : currentNode.getNextNodes()) {
					next.removePrevNode(currentNode);
					next.addPrevNode(loop);
					loop.addNextNode(next);
			}
			
			for (Node prev : currentNode.getPrevNodes()) {
					prev.removeNextNode(currentNode);
					prev.addNextNode(loop);
					loop.addPrevNode(prev);
			}
			
			assert(workingSet.contains(currentNode));
			workingSet.remove(currentNode);
			workingSet.add(loop);
		
			return true;
		} 
		return false;
	}

	public boolean usesDominatorTree() {
		return false;
	}

}
