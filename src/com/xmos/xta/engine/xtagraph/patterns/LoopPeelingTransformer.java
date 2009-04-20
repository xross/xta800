package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import java.util.Collections;
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
		
		System.out.println(currentNode + " has as its loops: ");
		for(List<Node> loop : loops){
			System.out.print("[");
			for(Node node : loop){
				if(node.getNextNodes().size() > 1){
					System.out.print("*" + node + " ");
				} else {
					System.out.print(" " + node + " ");
				}
				
			}
			System.out.println("]");
		}
		System.out.println();
		
		//never split the header
		
		if(loops.size() == 1){
			//choose one of the nodes with more then one loop exit to be the loop header
			
			
			
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//if there are multiple ways through the loop then it is not ready.
/*
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
			
			if(connectionNode == null){
				System.out.println("connectionNode problem");
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
			
			for(Node loopNode : loop){
				if(loopNode != connectionNode && loopEntryNodes.contains(loopNode)) {
					int breakIndex = loop.indexOf(loopNode);
					List<Node> nodesToPeel = loop.subList(breakIndex, loop.size());
					if(loop.subList(breakIndex, loop.size()).size() == 0){
						System.out.println("eek");
					}
					
					unpeelForwardGraph(connectionNode, loop.get((breakIndex+1)%loop.size()), nodesToPeel, workingSet, loop);
					return true;
				}
			}
					
			Collections.reverse(loop);
			for(Node loopNode : loop){
				if(loopNode != connectionNode && loopExitNodes.contains(loopNode)) {
					Collections.reverse(loop);
					int breakIndex = loop.indexOf(loopNode);
					
					if(loop.subList(1, breakIndex+1).size() == 0){
						System.out.println("eek");
					}
					
					List<Node> nodesToPeel = loop.subList(1, breakIndex+1);
					unpeelBackwardGraph(connectionNode, loop.get((breakIndex+1)%loop.size()), nodesToPeel, workingSet);
					return true;
				}
			}
	
		}
		
		*/
		return false;
	}
	/*
	protected void unpeelBackwardGraph(Node connectionNode, Node startOfBreak, List<Node> nodesToPeel, Set<Node> workingSet) {
		//break the connection between startOfBreak and the first of the nodes to peel
		Node endOfOldLoop = nodesToPeel.get(nodesToPeel.size() -1);
		
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
		
	}
	
	
	protected void unpeelForwardGraph(Node connectionNode, Node startOfBreak, List<Node> nodesToPeel, Set<Node> workingSet, List<Node> loop) {
		

		//as new nodes are being created all the ins to the old ones should now point to the new ones
		
		//but all the outs of the old ones should be duplicated on to the new ones.
		
		Node previous = null;
		Collection<Node> newNodes = new HashSet<Node>();
		for(Node peel : nodesToPeel){
			Node newNode = peel.clone();
			newNodes.add(newNode);
			
			Collection<Node> toRemove = new HashSet<Node>();
			for(Node prev : peel.getPrevNodes()){
				if(!loop.contains(prev)){
					
					newNode.addPrevNode(prev);
					prev.addNextNode(newNode);
					
					prev.getNextNodes().remove(peel);
					toRemove.add(prev);
				}
			}
			if(previous != null){
				previous.addNextNode(newNode);
				newNode.addPrevNode(previous);
			}
			previous = newNode;
			
			peel.getPrevNodes().removeAll(toRemove);
		}
		previous.addNextNode(connectionNode);
		connectionNode.addPrevNode(previous);
		workingSet.addAll(newNodes);
	}
	*/
	private void followNode(Node node, Node startNode, Collection<List<Node>> loops, List<Node> currentList){
		//if(loops.size() > 1 ) return;
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
