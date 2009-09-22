package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.TreeSet;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;

public class CommonMethods {
	
	public static void disconnect(Node leaf, Node root){
		root.removeNextNode(leaf);
		leaf.removePrevNode(root);
	}
	
	public static void connect(Node leaf, Node root){
		root.addNextNode(leaf);
		leaf.addPrevNode(root);
	}
	
	public static boolean wedgeUp(Node node, Collection<Node> workingSet){

		if (node.getNextNodesSize() < 2) {
			return false;
		}
		Collection<Node> newNodes = new TreeSet<Node>();
		
		for (Node next : node.getNextNodes()) {
			Node newNode = node.shallowCopy();

			newNodes.add(newNode);
			next.addPrevNode(newNode);
			next.removePrevNode(node);
			newNode.addNextNode(next);
		}

		for (Node newNode : newNodes){
			for (Node prev : node.getPrevNodes()) {
				newNode.addPrevNode(prev);
				prev.addNextNode(newNode);
			}
		}
		
		for (Node prev : node.getPrevNodes()) {
			prev.removeNextNode(node);
		}
		
		workingSet.remove(node);
		workingSet.addAll(newNodes);		
		return true;
	}
		
	//TODO find out why these functions aren't symmetric
	
	
	public static boolean wedgeDown(Node node, Collection<Node> workingSet){

		if (node.getPrevNodesSize() < 2) {
			return false;
		}
		
		Collection<Node> newNodes = new TreeSet<Node>();
		
		for (Node prev : node.getPrevNodes()){
			
			//make the new node
			Node newNode = node.shallowCopy();
			newNodes.add(newNode);

			prev.removeNextNode(node);
			
			//prev.getNextNodes().remove(node);
			prev.addNextNode(newNode);
			
			//add a connection to the new node
			newNode.addPrevNode(prev);
					
			for(Node next : node.getNextNodes()){
				if(next != node){
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
	
	public static Node findNullNode(Collection<Node> workingSet){
		for(Node node : workingSet){
			if(node.isNullNode()){
				return node;
			}
		}
		//didn't find one so make a new one!!!!
		Node nullNode = new NullNode();
		
		for(Node node : workingSet){
			if(node.getNextNodesSize() == 0){
				CommonMethods.connect(nullNode, node);
			}
		}
		
		//must add all other terminals to it
		workingSet.add(nullNode);
		return nullNode;
	}
		
}
