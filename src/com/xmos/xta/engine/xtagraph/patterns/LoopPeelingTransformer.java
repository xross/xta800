package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import com.xmos.xta.engine.xtagraph.nodes.Node;

public class LoopPeelingTransformer extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet) {
		
		//first detect all loops from the current node
		
		LinkedList<List<Node>> loops = new LinkedList<List<Node>>();
		

		followNode(currentNode, currentNode, loops, new LinkedList<Node>());
		if(loops.size() == 0) return false;
		
		//System.out.println("# " + currentNode + " has as its loops: ");
		for(List<Node> loop : loops){
			//System.out.print("#[");
			for(Node node : loop){
				if(node.getNextNodes().size() > 1){
					//System.out.print("*" + node + " ");
				} else {
				//	System.out.print(" " + node + " ");
				}
				

			}
			
			//System.out.println("]");
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
			/*
			if(connectionPoints.size() == 0){
				for(Node loopNode : loop){
					if(loopNode.getPrevNodes().size() > 1){
						connectionPoints.add(loopNode);
					}
				}
			}
			*/
			//connectionPoints now is a set of all the points that could be a connectionNode
			
			//System.out.println("# connectionPoints are : " + connectionPoints);
			
			//System.out.println("# entryPoints are : " + entryPoints);
			//if(connectionPoints.size() == 1){
				//we can actually split it
				for(Node node : loop){
					if(!connectionPoints.contains(node) && entryPoints.contains(node)){
						CommonMethods.wedgeDown(node, workingSet);
						return true;
					}
					if(!connectionPoints.contains(node) && exitPoints.contains(node)){
						CommonMethods.wedgeUp(node, workingSet);
						return true;
					}
				}
				
				
			//}
			
		}
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
