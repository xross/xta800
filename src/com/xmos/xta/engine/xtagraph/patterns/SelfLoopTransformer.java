package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Set;

import com.xmos.xta.engine.xtagraph.nodes.Node;

public class SelfLoopTransformer  extends PatternMatcher {


	public boolean isMatch(Node currentNode, Set<Node> workingSet) {
		if(currentNode.getNextNodes().contains(currentNode)){
			Node newNode = currentNode.clone();
			newNode.addNextNode(currentNode);
			newNode.addPrevNode(currentNode);
			currentNode.getNextNodes().remove(currentNode);
			currentNode.getPrevNodes().remove(currentNode);
			currentNode.addNextNode(newNode);
			currentNode.addPrevNode(newNode);
			workingSet.add(newNode);
			
			return true;
		} else {
			return false;
		}
	}

}
