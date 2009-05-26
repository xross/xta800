package com.xmos.xta.engine.analysis.patterns;

import java.util.Collection;

import com.xmos.xta.engine.analysis.nodes.Node;


public class SelfLoopTransformer  extends PatternMatcher {


	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		m_treeStart = treeStart;
		
		if(currentNode.getNextNodes().contains(currentNode)){
			Node newNode = currentNode.clone();
			newNode.addNextNode(currentNode);
			newNode.addPrevNode(currentNode);
			currentNode.getNextNodes().remove(currentNode);
			currentNode.getPrevNodes().remove(currentNode);
			currentNode.addNextNode(newNode);
			currentNode.addPrevNode(newNode);
			workingSet.add(newNode);
			
			if(verbose){
				System.out.println("#found a self loop at " + currentNode);
			}
			
			return true;
		} else {
			return false;
		}
	}

}
