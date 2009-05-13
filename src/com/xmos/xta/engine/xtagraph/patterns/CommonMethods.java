package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.HashSet;

import com.xmos.xta.engine.xtagraph.nodes.Node;

public class CommonMethods {
	static private final boolean DEBUG = false;
	public static boolean isTerminal(Node node){
		if(node.getNextNodes().size() == 0){
			return true;
		} else {
			return false;
		}
	}
	
	public static void disconnect(Node leaf, Node root){
		root.getNextNodes().remove(leaf);
		leaf.getPrevNodes().remove(root);
	}
	
	public static void connect(Node leaf, Node root){
		root.addNextNode(leaf);
		leaf.addPrevNode(root);
	}
	
	public static void wedgeUp(Node node, Collection<Node> workingSet, boolean verbose){
		if(verbose) {
			System.out.println("#am wedging up on node " + node);
		}
		Collection<Node> newNodes = new HashSet<Node>();
		
		for(Node next : node.getNextNodes()){
			
			//make the new node
			Node newNode = node.clone();
			newNodes.add(newNode);
			
			//remove the connection from the old node
			next.getPrevNodes().remove(node);
			next.addPrevNode(newNode);
			
			//add a connection to the new node
			newNode.addNextNode(next);
			
			for(Node prev : node.getPrevNodes()){
				if(prev != node){
					prev.getNextNodes().remove(node);
					prev.addNextNode(newNode);
					newNode.addPrevNode(prev);
				}
			}
		}
		workingSet.remove(node);
		workingSet.addAll(newNodes);
	}
	
	public static void wedgeDown(Node node, Collection<Node> workingSet, boolean verbose){
		if(verbose) {
			System.out.println("#am wedging down on node " + node);
		}
		Collection<Node> newNodes = new HashSet<Node>();
		
		for(Node prev : node.getPrevNodes()){
			
			//make the new node
			Node newNode = node.clone();
			newNodes.add(newNode);
			
			//remove the connection from the old node
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
		
		return;
	}
	
	public static void wedgeDown(Node node, Node piviot, Collection<Node> workingSet){
		if (DEBUG) System.out.println("#wedge down on " + node);
		assert(node.getPrevNodes().contains(piviot));
		
		Node newNode = node.clone();
		
		piviot.getNextNodes().remove(node);
		node.getPrevNodes().remove(piviot);
		
		piviot.addNextNode(newNode);
		newNode.addPrevNode(piviot);
		
		for(Node next : node.getNextNodes()){
			newNode.addNextNode(next);
			next.addPrevNode(newNode);
		}
		
		workingSet.add(newNode);
	}
	
	public static void wedgeUp(Node node, Node piviot, Collection<Node> workingSet){
		assert(node.getNextNodes().contains(piviot));
		if (DEBUG) System.out.println("#wedge up on " + node);
		Node newNode = node.clone();
		
		piviot.getPrevNodes().remove(node);
		node.getNextNodes().remove(piviot);
		
		piviot.addPrevNode(newNode);
		newNode.addNextNode(piviot);
		
		for(Node prev : node.getPrevNodes()){
			newNode.addPrevNode(prev);
			prev.addNextNode(newNode);
		}
		
		workingSet.add(newNode);
	}
}
