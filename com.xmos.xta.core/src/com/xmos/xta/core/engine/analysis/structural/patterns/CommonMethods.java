package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.controlflow.nodes.CfgNullNode;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class CommonMethods {

	public static boolean wedgeUp(CfgNode node, Collection<CfgNode> workingSet) {

		if (node.getNextNodesSize() < 2) {
			return false;
		}
		Collection<CfgNode> newNodes = new IteratableSet<CfgNode>();
		
		for (CfgNode next : node.getNextNodes()) {
			
			assert(node instanceof CfgContainerNode);
			Node strNode = ((CfgContainerNode)node).getNode().copyRouteStructure();
			CfgNode newNode = new CfgContainerNode(strNode);								
			
			newNodes.add(newNode);
			next.addPrevNode(newNode);
			next.removePrevNode(node);
			newNode.addNextNode(next);
		}

		for (CfgNode newNode : newNodes){
			for (CfgNode prev : node.getPrevNodes()) {
				newNode.addPrevNode(prev);
				prev.addNextNode(newNode);
			}
		}

		for (CfgNode prev : node.getPrevNodes()) {
			prev.removeNextNode(node);
		}

		workingSet.remove(node);
		workingSet.addAll(newNodes);
		return true;
	}

	//TODO find out why these functions aren't symmetric

	public static boolean wedgeDown(CfgNode node, Collection<CfgNode> workingSet) {

		if (node.getPrevNodes().size() < 2) {
			return false;
		}

		Collection<CfgNode> newNodes = new IteratableSet<CfgNode>();
		for (CfgNode prev : node.getPrevNodes()){

			assert(node instanceof CfgContainerNode);
			Node strNode = ((CfgContainerNode)node).getNode().copyRouteStructure();
			CfgNode newNode = new CfgContainerNode(strNode);								

			newNodes.add(newNode);

			prev.removeNextNode(node);

			//prev.getNextNodes().remove(node);
			prev.addNextNode(newNode);

			//add a connection to the new node
			newNode.addPrevNode(prev);

			for (CfgNode next : node.getNextNodes()) {
				if (next != node) {
					next.removePrevNode(node);
					next.addPrevNode(newNode);
					newNode.addNextNode(next);
				}
			}
		}

		workingSet.remove(node);
		workingSet.addAll(newNodes);
		return true;
	}

	public static Set<Node> getAllContainedNodes(Node node) {
		Set<Node> seen = new IteratableSet<Node>();
		seen.add(node);
		for(Node child : node.iChildren()){
			seen.addAll(getAllContainedNodes(child));
		}
		
		return seen;
	}
	
	public static CfgNode findNullNode(Collection<CfgNode> workingSet){
		for (CfgNode node : workingSet){
			if (node instanceof CfgNullNode) {
				return node;
			}
		}
		//didn't find one so make a new one!!!!
		CfgNode nullNode = new CfgNullNode();

		for (CfgNode node : workingSet){
			if (node.getNextNodesSize() == 0){
				CfgNode.connect(nullNode, node);
			}
		}

		//must add all other terminals to it
		workingSet.add(nullNode);
		return nullNode;
	}	
}
