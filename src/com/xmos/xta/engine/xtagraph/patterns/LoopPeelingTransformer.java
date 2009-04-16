package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.xtagraph.nodes.Loop;
import com.xmos.xta.engine.xtagraph.nodes.Node;

public class LoopPeelingTransformer extends PatternMatcher {

	public boolean isMatch(Node currentNode, Set<Node> workingSet) {
		
		//first detect all loops from the current node
		
		LinkedList<List<Node>> loops = new LinkedList<List<Node>>();
		

		followNode(currentNode, currentNode, loops, new LinkedList<Node>());
		
		//if there are multiple ways through the loop then it is not ready.

		if(loops.size() == 1 ){
			//now identify the farthest entry to the loop to the currentNode
			List<Node> loop = loops.getFirst();
			
			//System.out.println("currentNode is: " +currentNode );
			Collections.reverse(loop);
			

			Node connectionNode = null;
			for(Node loopNode : loop){
				
				if(loopNode.getPrevNodes().size() > 1){
					connectionNode = loopNode;
					break;
				}
			}
			Collections.reverse(loop);
			
			
			//the list need rotating by the distance the entry point is from the currentNode
			int distance = loop.indexOf(connectionNode);
			
			
			
			Collections.rotate(loop, -distance);
			
			
			//never reproduce the connectionNode but do change where it links too
			//System.out.println(loop);
			
			//this holds a record of the nodes which enter and exit the loop but
			// not via the loop header
			Collection<Node> loopEntryNodes = new HashSet<Node>();
			Collection<Node> loopExitNodes = new HashSet<Node>();
			
			for(Node loopNode : loop){
				if(loopNode != connectionNode) {
					for(Node prev : loopNode.getPrevNodes()){
						if(!loop.contains(prev)){
							loopEntryNodes.add(loopNode);
						}
					}
					for(Node next : loopNode.getNextNodes()){
						if(!loop.contains(next)){
							loopExitNodes.add(loopNode);
						}
					}
				}
			}
			
			/*
			 * in the direction of the loop we have to find the closest loop entry from an out side source
			 */
			/*
			for(Node loopNode : loop){
				if(loopNode != connectionNode && loopEntryNodes.contains(loopNode)) {
					//System.out.println("this loop needs peeling from " + loopNode + " to the connectionNode");
					
					int breakIndex = loop.indexOf(loopNode);
					List<Node> nodesToPeel = loop.subList(breakIndex, loop.size());
					//System.out.println(nodesToPeel);
					
					break;
					//unpeelForwardGraph(connectionNode, loop.get(breakIndex-1), nodesToPeel, workingSet);
					
					//return true;
				}
			}
			*/
			
			Collections.reverse(loop);
			for(Node loopNode : loop){
				if(loopNode != connectionNode && loopExitNodes.contains(loopNode)) {
					//System.out.println("this loop needs peeling from " + loopNode + " to the connectionNode");
					Collections.reverse(loop);
					int breakIndex = loop.indexOf(loopNode);
					List<Node> nodesToPeel = loop.subList(1, breakIndex+1);
					//System.out.println(nodesToPeel);
					
					
					unpeelBackwardGraph(connectionNode, loop.get(breakIndex+1), nodesToPeel, workingSet);
					
					return true;
				}
			}
			
			//System.out.println();
		}
		
		
		return false;
	}
	
	protected void unpeelBackwardGraph(Node connectionNode, Node startOfBreak, List<Node> nodesToPeel, Set<Node> workingSet) {
		//break the connection between startOfBreak and the first of the nodes to peel
		Node endOfOldLoop = nodesToPeel.get(nodesToPeel.size() -1);
		
		//System.out.println("start of break : " + startOfBreak);
		//System.out.println("end of loop    : " + endOfOldLoop);
		//System.out.println("connectionNode: "+ connectionNode);
		//System.out.println("nodes to peel: " + nodesToPeel);
		
		
		Node current = connectionNode;
		
		Collection<Node> newNodes = new HashSet<Node>();
		for(Node oldPath : nodesToPeel){
			
			Node newNode = oldPath.clone();
			
			Collection<Node> nodesToDisconnectFrom = new HashSet<Node>();
			
			for(Node prev : oldPath.getPrevNodes()){
				if(!nodesToPeel.contains(prev) && prev != connectionNode){
					//any nodes connection to the outer loop from outside the loop
					//should now be connected to the inner loop
					newNode.addPrevNode(prev);
					prev.addNextNode(newNode);
					
					nodesToDisconnectFrom.add(prev);
					
					//and disconnected from the outer loop
					//prev.getNextNodes().remove(oldPath);
					//oldPath.getPrevNodes().remove(prev);
					
					
					//System.out.println("node " + prev + " connects from the outside to the new node");
				}
			}
			
			for(Node prev : nodesToDisconnectFrom){
				prev.getNextNodes().remove(oldPath);
				oldPath.getPrevNodes().remove(prev);
			}
			
			current.addNextNode(newNode);
			newNode.addPrevNode(current);
			current = newNode;
			newNodes.add(newNode);
			
		}
		current.addNextNode(startOfBreak);
		startOfBreak.addPrevNode(current);
		endOfOldLoop.getNextNodes().remove(startOfBreak);
		startOfBreak.getPrevNodes().remove(endOfOldLoop);
		workingSet.addAll(newNodes);
		
		
		/*
		endOfOldLoop.getPrevNodes().remove(startOfBreak);
		startOfBreak.getNextNodes().remove(endOfOldLoop);
		

		Collection<Node> newNodes = new HashSet<Node>();
		Node prev = startOfBreak;
		for(Node newPath : nodesToPeel){
			Node newNode = newPath.clone();
			newNodes.add(newNode);
			newNode.addPrevNode(prev);
			prev.addNextNode(newNode);
			prev = newNode;
		}
		connectionNode.addPrevNode(prev);
		prev.addNextNode(connectionNode);
		workingSet.addAll(newNodes);
		*/
	}
	
	
	protected void unpeelForwardGraph(Node connectionNode, Node startOfBreak, List<Node> nodesToPeel, Set<Node> workingSet) {
		//break the connection between startOfBreak and the first of the nodes to peel
		Node endOfOldLoop = nodesToPeel.get(nodesToPeel.size() -1);
		
		endOfOldLoop.getPrevNodes().remove(startOfBreak);
		startOfBreak.getNextNodes().remove(endOfOldLoop);
		
		/*
		 * now make a new path from the one we just disconnected
		 */
		Collection<Node> newNodes = new HashSet<Node>();
		Node prev = startOfBreak;
		for(Node newPath : nodesToPeel){
			Node newNode = newPath.clone();
			newNodes.add(newNode);
			newNode.addPrevNode(prev);
			prev.addNextNode(newNode);
			prev = newNode;
		}
		connectionNode.addPrevNode(prev);
		prev.addNextNode(connectionNode);
		workingSet.addAll(newNodes);
	}
	
	
	
	private void followNode(Node node, Node startNode, Collection<List<Node>> loops, List<Node> currentList){
		for(Node next : node.getNextNodes()){
			List<Node> newList = new LinkedList<Node>(currentList);
			newList.add(next);
			if(next == startNode){
				//we have found a loop
				loops.add(newList);
			} else {
				
				if(!currentList.contains(next))
					followNode(next, startNode, loops, newList);
			}
		}
	}
	
	
}
