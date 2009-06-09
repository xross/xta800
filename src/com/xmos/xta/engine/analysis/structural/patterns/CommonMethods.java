package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.HashSet;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class CommonMethods {
	
	public static void disconnect(Node leaf, Node root){
		root.getNextNodes().remove(leaf);
		leaf.getPrevNodes().remove(root);
	}
	
	public static void connect(Node leaf, Node root){
		root.addNextNode(leaf);
		leaf.addPrevNode(root);
	}
	
	public static boolean wedgeUp(Node node, Collection<Node> workingSet){

		if (node.getNextNodes().size() < 2) {
			return false;
		}
		Collection<Node> newNodes = new HashSet<Node>();
		
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
		
	public static boolean wedgeDown(Node node, Collection<Node> workingSet){

		if (node.getPrevNodes().size() < 2) {
			return false;
		}
		
		Collection<Node> newNodes = new HashSet<Node>();
		
		for (Node prev : node.getPrevNodes()){
			
			//make the new node
			Node newNode = node.shallowCopy();
			newNodes.add(newNode);

			prev.getNextNodes().remove(node);
			prev.addNextNode(newNode);
			
			//add a connection to the new node
			newNode.addPrevNode(prev);
					
			for(Node next : node.getNextNodes()){
				if(next != node){
					next.getPrevNodes().remove(node);
					next.addPrevNode(newNode);
					newNode.addNextNode(next);
				}
			}
		}
		
		workingSet.remove(node);
		workingSet.addAll(newNodes);		
		return true;
	}
		
}
