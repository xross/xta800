package com.xmos.xta.engine.xtagraph.patterns;

import java.util.LinkedList;
import java.util.Set;
import com.xmos.xta.engine.xtagraph.nodes.Node;
import com.xmos.xta.engine.xtagraph.nodes.Path;

public class PathPatternMatcher extends PatternMatcher {

	public boolean isMatch(Node startNode, Set<Node> workingSet) {
		Node currentNode = startNode;
		
		//must grow in both directions in order to avoid the need to consolidate path
		//together
		LinkedList<Node> pathList = new LinkedList<Node>();
		
		//a path is defined to be a sequence of nodes such that the head
		//has more than one (or zero) entering edges and the end node has more then one 
		//(or zero) exiting edges.
		
		
		
		pathList.add(currentNode);
		boolean finished = false;
		while(!finished){
			if(currentNode.getNextNodes().size() == 1){
				Node nextNode = new LinkedList<Node>(currentNode.getNextNodes()).getFirst();
				if(nextNode.getPrevNodes().size() == 1 && !nextNode.getNextNodes().contains(currentNode)){
					pathList.add(nextNode);
					currentNode = nextNode;
				} else {
					finished = true;
				}
			} else {
				finished = true;
			}
		}
		currentNode = startNode;
		finished = false;
		while(!finished){
			if(currentNode.getPrevNodes().size() == 1){
				Node prevNode = new LinkedList<Node>(currentNode.getPrevNodes()).getFirst();
				if(prevNode.getNextNodes().size() == 1 && !prevNode.getPrevNodes().contains(currentNode)){
					pathList.addFirst(prevNode);
					currentNode = prevNode;
				} else {
					finished = true;
				}
			} else {
				finished = true;
			}
		}
		if(pathList.size() > 1){
			
		//	System.out.println("# starting with " + startNode);
		//	System.out.println("# found a path of " + pathList);
			
			updateGraph(pathList, workingSet);
			return true;
		} else {
			return false;
		}
		//grow forward
		/*
		boolean done = false;
		pathList.add(currentNode);
		
		while(!done){
			if(currentNode.getNextNodes().size() == 1) {
				Node next = new LinkedList<Node>(currentNode.getNextNodes()).getFirst();
				if(next.getPrevNodes().size() == 1){
					pathList.add(next);
					currentNode = next;
				} else {
					done = true;
				}
			} else {
				done = true;
			}
		}
		
		done = false;
		
		currentNode = startNode;
		
		while(!done){
			if(currentNode.getPrevNodes().size() == 1) {
				Node prev = new LinkedList<Node>(currentNode.getPrevNodes()).getFirst();
				if(prev.getNextNodes().size() == 1){
					pathList.addFirst(prev);
					currentNode = prev;
				} else {
					done = true;
				}
			} else {
				done = true;
			}
		}
		
		if(pathList.size() > 1){
			updateGraph(pathList, workingSet);
			return true;
		} else {
			return false;
		}
		*/
	}

	protected void updateGraph(LinkedList<Node> pathList, Set<Node> workingSet) {
		//System.out.println("#have found a path of " + pathList);
		Path path = new Path(pathList);
		
		Node startNodeInPath = pathList.getFirst();
		Node endNodeInPath = pathList.getLast(); 
		
		for(Node prev : startNodeInPath.getPrevNodes()){
			path.addPrevNode(prev);
			if(!prev.getNextNodes().remove(startNodeInPath)){
				System.out.println("####################### this is bad ############################################################");
			}
			prev.addNextNode(path);
		}
		for(Node next : endNodeInPath.getNextNodes()){
			path.addNextNode(next);
			if(!next.getPrevNodes().remove(endNodeInPath)){
				System.out.println("######################## this is bad ###########################################################");
			}
			
			next.addPrevNode(path);
		}
		
		workingSet.removeAll(pathList);
		workingSet.add(path);
	}
	
	
}
