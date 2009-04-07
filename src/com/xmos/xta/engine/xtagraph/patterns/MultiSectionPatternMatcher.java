package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.xmos.xta.engine.xtagraph.nodes.MultiSection;
import com.xmos.xta.engine.xtagraph.nodes.Node;

public class MultiSectionPatternMatcher extends PatternMatcher {


	public boolean isMatch(Node node, Set<Node> workingSet) {
		
		Collection<Node> nextNodes = node.getNextNodes();
		if(nextNodes.size()  < 2){
			return false;
		}
		
		//if every next node points to a node that points to node x and only x then we have a multiSection.
		Collection<Node> nextNextNodes = new HashSet<Node>();
		Collection<Node> sectionNodes = new HashSet<Node>();
		
		Node jumpNode = null;
		for(Node nextNode : nextNodes){
			//if nextNodes previous nodes count is > 1 then it must be the special jump node
			if(nextNode.getPrevNodes().size() == 1){
				nextNextNodes.addAll(nextNode.getNextNodes());
				sectionNodes.add(nextNode);
			} else {
				if(jumpNode != null && nextNode != jumpNode){
					return false;
				} else {
					jumpNode = nextNode;
				}
			}
		}
		if(nextNextNodes.size() == 1 && (nextNextNodes.contains(jumpNode)|| jumpNode == null)){
			updateGraph(node, sectionNodes, jumpNode, workingSet);
			return true;
		} else {
			return false;
		}
	}

	protected void updateGraph(Node node, Collection<Node> sectionNodes, Node jumpNode, Set<Node> workingSet) {
		
		MultiSection ms = new MultiSection(sectionNodes, jumpNode);
		
		node.getNextNodes().clear();
		node.addNextNode(ms);
		
		workingSet.removeAll(sectionNodes);
		sectionNodes.add(ms);
		
	}

	
	
	
}
