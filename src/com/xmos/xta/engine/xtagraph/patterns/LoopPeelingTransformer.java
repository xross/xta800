package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.xtagraph.nodes.Node;

public class LoopPeelingTransformer extends PatternMatcher {

	
	
	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose) {
		Set<Node> leastDominantNodes = new HashSet<Node>();
		for(Node node : workingSet){
			for(Node next : node.getNextNodes()){
				if(node.getDominators().contains(next)){
					leastDominantNodes.add(node);
				}
			}
		}
		for(Node ld : leastDominantNodes){
			System.out.println("# " +ld + " is a least dominant node");
		}
		if(leastDominantNodes.contains(currentNode)){
			System.out.println("#would like to loop peel " + currentNode);
			CommonMethods.wedgeDown(currentNode, workingSet, verbose);
			return true;
		}
		
		
		
		/*
		

		//first detect all loops from the current node
		
		LinkedList<List<Node>> loops = new LinkedList<List<Node>>();
		

		followNode(currentNode, currentNode, loops, new LinkedList<Node>());
		if(loops.size() == 0) return false;

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
			for(Node node : loop){
				if(!connectionPoints.contains(node) && entryPoints.contains(node)){
					if(verbose){
						System.out.println("#trying to peel for a loop");
					}
					CommonMethods.wedgeDown(node, workingSet, verbose);
					return true;
				}
				if(!connectionPoints.contains(node) && exitPoints.contains(node)){
					if(verbose){
						System.out.println("#trying to peel for a loop");
					}
					CommonMethods.wedgeUp(node, workingSet, verbose);
					return true;
				}
			}
			
		}
		*/
		return false;
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
