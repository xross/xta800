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
		
		//grow forward
		if(currentNode.getNextNodes().size() == 1) {
			pathList.add(currentNode);
			//currentNode = next;
			currentNode = new LinkedList<Node>(currentNode.getNextNodes()).getFirst();
			
			while(currentNode.getNextNodes().size() == 1 
					&& currentNode.getPrevNodes().size() == 1 && !pathList.contains(currentNode)) {
				pathList.add(currentNode);
				//currentNode = next;
				currentNode = new LinkedList<Node>(currentNode.getNextNodes()).getFirst();
			}
		} else if(currentNode.getNextNodes().size() == 0){
			pathList.add(currentNode);
		}
		
		
		//grow backwards
		currentNode = startNode;
		boolean done = false;
		
		while(!done){
			if(currentNode.getPrevNodes().size() == 1){
				//if the previous node has one out then add it and make it the current node
				Node previousNode = new LinkedList<Node>(currentNode.getPrevNodes()).getFirst();
				if(previousNode.getNextNodes().size() == 1 && !pathList.contains(previousNode)) {
					pathList.add(previousNode);
					currentNode = previousNode;
				} else {
					done = true;
				}		
			} else if(currentNode.getPrevNodes().size() == 0){
				if(pathList.isEmpty())
					pathList.add(currentNode);
				else {
					if(!pathList.contains(currentNode))
						pathList.addFirst(currentNode);
				}
				done = true;
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
	}

	protected void updateGraph(LinkedList<Node> pathList, Set<Node> workingSet) {
		
		Path path = new Path(pathList);
		
		Node startNodeInPath = pathList.getFirst();
		Node endNodeInPath = pathList.getLast(); 
		
		for(Node prev : startNodeInPath.getPrevNodes()){
			path.addPrevNode(prev);
			prev.getNextNodes().remove(startNodeInPath);
			prev.addNextNode(path);
		}
		for(Node next : endNodeInPath.getNextNodes()){
			path.addNextNode(next);
			next.getPrevNodes().remove(endNodeInPath);
			next.addPrevNode(path);
		}

		workingSet.removeAll(pathList);
		workingSet.add(path);
		System.out.println("adding path " + pathList);
	}
	
	
}
