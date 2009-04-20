package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.xmos.xta.engine.xtagraph.nodes.Node;

public class LoopPeelingTransformer extends PatternMatcher {

	public boolean isMatch(Node currentNode, Set<Node> workingSet) {
		
		//first detect all loops from the current node
		
		LinkedList<List<Node>> loops = new LinkedList<List<Node>>();
		

		followNode(currentNode, currentNode, loops, new LinkedList<Node>());
		if(loops.size() == 0) return false;
		
		//System.out.println("# " + currentNode + " has as its loops: ");
		for(List<Node> loop : loops){
			//System.out.print("#[");
			for(Node node : loop){
				if(node.getNextNodes().size() > 1){
				//	System.out.print("*" + node + " ");
				} else {
				//	System.out.print(" " + node + " ");
				}
				

			}
			
			//System.out.println("]");
			//System.out.println(weighLoopOut(loop) + " " + weighLoopIn(loop));
		}
		
		
		//never split the header

		for(List<Node> loop : loops){
			Collection<Node> exitPoints= new HashSet<Node>();
			Collection<Node> entryPoints = new HashSet<Node>();
			
			for(Node loopNode : loop){
				if(loopNode.getPrevNodes().size() > 1){
					entryPoints.add(loopNode);
				}
				if(loopNode.getNextNodes().size() > 1){
					exitPoints.add(loopNode);
				}
			}
			
			Collection<Node> connectionPoints= new HashSet<Node>();
			
			for(Node loopNode : loop){
				if(loopNode.getPrevNodes().size() > 1 && loopNode.getNextNodes().size() > 1){
					connectionPoints.add(loopNode);
				}
			}
			if(connectionPoints.size() == 0){
				for(Node loopNode : loop){
					if(loopNode.getNextNodes().size() > 1){
						connectionPoints.add(loopNode);
					}
				}
			}
			//connectionPoints now is a set of all the points that could be a connectionNode
			
			//System.out.println("# connectionPoints are : " + connectionPoints);
			if(connectionPoints.size() == 1){
				//we can actually split it
				for(Node node : loop){
					if(!connectionPoints.contains(node) && entryPoints.contains(node)){
						wedgeDown(node, workingSet);
						return true;
					}
					if(!connectionPoints.contains(node) && exitPoints.contains(node)){
						wedgeUp(node, workingSet);
						return true;
					}
				}
				
				
			}
			
			//ideally these sets both have one node
			
			//System.out.println("#entry: " + entryPoints + " exit: " + exitPoints);
			
			
		}
		
		
		//System.out.println();
		return false;
	}

	private void wedgeUp(Node node, Set<Node> workingSet){
		Collection<Node> newNodes = new HashSet<Node>();
		for(Node next: node.getNextNodes()){
			Node newNode = node.clone();
			newNodes.add(newNode);
			
			next.getPrevNodes().remove(node);
			next.addPrevNode(newNode);
			newNode.addNextNode(next);
			
			for(Node prev: node.getPrevNodes()){
				newNode.addPrevNode(prev);
				prev.getNextNodes().remove(node);
				prev.addNextNode(newNode);
			}
			
		}
		
		workingSet.remove(node);
		workingSet.addAll(newNodes);
	}
	
	private void wedgeDown(Node node, Set<Node> workingSet){
	
		Collection<Node> newNodes = new HashSet<Node>();
		for(Node prev: node.getPrevNodes()){
			Node newNode = node.clone();
			newNodes.add(newNode);
			
			prev.getNextNodes().remove(node);
			prev.addNextNode(newNode);
			newNode.addPrevNode(prev);
			
			for(Node next: node.getNextNodes()){
				newNode.addNextNode(next);
				next.getPrevNodes().remove(node);
				next.addPrevNode(newNode);
			}
			
		}
		
		workingSet.remove(node);
		workingSet.addAll(newNodes);
		
		return;
	}
	
	
	private void followNode(Node node, Node startNode, Collection<List<Node>> loops, List<Node> currentList){
		for(Node next : node.getNextNodes()){
			if (currentList.contains(next))
				continue;
			currentList.add(next);
			if(next == startNode){
				loops.add(new LinkedList<Node>(currentList));
			} else {
				followNode(next, startNode, loops, currentList);
			}
			currentList.remove(currentList.size()-1);
		}
	}
	
}
