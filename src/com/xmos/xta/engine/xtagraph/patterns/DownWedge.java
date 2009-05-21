package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.xmos.xta.engine.xtagraph.nodes.Node;

public class DownWedge extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		m_treeStart = treeStart;
		
		if(currentNode.getPrevNodes().size()  < 2 ){
			return false;
		}
		
		Set<Node> leastDominantNodes = new HashSet<Node>();
		for(Node node : workingSet){
			for(Node next : node.getNextNodes()){
				if(node.getDominators().contains(next)){
					leastDominantNodes.add(node);
				}
			}
		}
		if(leastDominantNodes.isEmpty()){
			return false;
		}
		if(leastDominantNodes.contains(currentNode)){			
			return CommonMethods.wedgeDown(currentNode, workingSet, verbose);
		}
		return false;
	}
	
}
