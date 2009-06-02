package com.xmos.xta.engine.analysis.patterns;

import java.util.Collection;
import java.util.HashSet;
import com.xmos.xta.engine.analysis.nodes.Node;

public class CommonMethods {
	public static void disconnect(Node leaf, Node root){
		root.getNextNodes().remove(leaf);
		leaf.getPrevNodes().remove(root);
	}
	
	public static void connect(Node leaf, Node root){
		root.addNextNode(leaf);
		leaf.addPrevNode(root);
	}
	
	public static boolean wedgeUp(Node node, Collection<Node> workingSet, boolean verbose){
		if (verbose) {
			System.out.println("#am wedging up on node " + node);
		}
		if (node.getNextNodes().size() < 2) {
			return false;
		}
		Collection<Node> newNodes = new HashSet<Node>();
		
		for (Node next : node.getNextNodes()) {
			Node newNode = node.clone();
			if (verbose) {
				System.out.println("#made a new node: " + newNode);
			}
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
		
	public static boolean wedgeDown(Node node, Collection<Node> workingSet, boolean verbose){
		if (verbose) {
			System.out.println("#am wedging down on node " + node);
		}

		if (node.getPrevNodes().size() < 2) {
			return false;
		}
		
		Collection<Node> newNodes = new HashSet<Node>();
		
		for (Node prev : node.getPrevNodes()){
			
			//make the new node
			Node newNode = node.clone();
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
		
	private static int graphcount;
	public static void printGraph(Collection<Node> nodes) {
		System.out.println("digraph g" + graphcount++ + " {");
		for (Node node : nodes) {

			System.out.print(node.graphVizLabel() + " [label =\"");
			System.out.print("Name: " + node.getInstructions() + "\\n");
			System.out.println("\"];");

		}
		for (Node node : nodes) {
			for (Node next : node.getNextNodes()) {
				System.out.println(node.graphVizLabel() + " -> "
						+ next.graphVizLabel() + ";");
			}
		}
		System.out.println("};");
	}
}
