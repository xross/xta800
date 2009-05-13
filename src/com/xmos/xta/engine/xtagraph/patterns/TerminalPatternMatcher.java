package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import com.xmos.xta.engine.xtagraph.nodes.Node;
import com.xmos.xta.engine.xtagraph.nodes.Terminal;

public class TerminalPatternMatcher extends PatternMatcher {

	

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose) {
		
		/*
		 * this needs to be modified so that it can only happen when the currentNode is the first node and the next nodes are all the terminating nodes
		 */
		
		//a node is a terminal node when all nodes after it terminate immediately
		
		if(currentNode.getNextNodes().size() < 2){
			return false;
		}
		for(Node next : currentNode.getNextNodes()){
			if(!(next.getNextNodes().size() == 0 && next.getPrevNodes().size() == 1)){
				return false;
			}
		}
		Terminal newNode = new Terminal(currentNode.getNextNodes());
		
		if(verbose){
			System.out.println("#found a terminal node from " + currentNode + " of " + currentNode.getNextNodes());
		}
		
		workingSet.removeAll(currentNode.getNextNodes());
		workingSet.add(newNode);
		currentNode.getNextNodes().clear();
		
		CommonMethods.connect(newNode, currentNode);
		
		
		
		
		return true;	
	}

}
