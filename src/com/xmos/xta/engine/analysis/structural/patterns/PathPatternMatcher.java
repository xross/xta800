package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.engine.analysis.structural.nodes.PathNode;

public class PathPatternMatcher extends PatternMatcher {	
	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart) {
		
		m_treeStart = treeStart;
		LinkedList<Node> pathList = new LinkedList<Node>();
		pathList.add(currentNode);
		if (isStart(currentNode)) {
			while (continueDown(currentNode, workingSet, pathList)) {
				currentNode = currentNode.getNextNode();
				pathList.add(currentNode);
			}
		}
				
		Node lastNode = pathList.getLast();
		if(lastNode.isNullNode() && pathList.size() > 1){
			CommonMethods.disconnect(lastNode, pathList.get(pathList.size()-2));
			pathList.removeLast();
			workingSet.remove(lastNode);
		}
		
		
		if (pathList.size() > 1) {
			assert(pathSanityCheck(pathList));
			updateGraph(pathList, workingSet, treeStart);
			return true;
		} else {
			return false;
		}
	}

	private boolean isStart(Node node){
		if(node == m_treeStart){
			return true;
		} else if (node.getPrevNodesSize() ==  0) {
			return true;
		} else if (node.getPrevNodesSize() ==  1) {
			Node prev = node.getPrevNode();
			if (prev.getNextNodesSize() > 1) {
				return true;
			} else {
				return false;
			}
		} else if (node.getPrevNodesSize() > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isEnd(Node node, Collection<Node> workingSet, LinkedList<Node> pathList) {
		
		if (node.getNextNodesSize() ==  0){
			return true;
		} else if (node.getNextNodesSize() ==  1) {
			Node next = node.getNextNode();
			
			if(next.isNullNode() && pathList.size() + 1 < workingSet.size()){
				return true;
			}
			
			
			if(next == m_treeStart){
				return true;
			}
			if (next.getPrevNodesSize() > 1) {
				return true;
			} else {
				return false;
			}
		} 
		
		//FIXME - i dont think this can be hit
		else if (node.getNextNodesSize() > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean continueDown(Node node, Collection<Node> workingSet, LinkedList<Node> pathList){
		if (node.getNextNodesSize() == 1 && !isEnd(node, workingSet, pathList) 
				&& !node.dominatorsContain(node.getNextNode())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean pathSanityCheck(LinkedList<Node> pathList) {
		Set<Node> seen = new TreeSet<Node>();
		
		if (pathList.size() < 2) {
			return false;
		}
		
		for (int i = 0; i < pathList.size() - 2; i++) {
			Node node = pathList.get(i);
			
			if (seen.contains(node)) {
				return false;
			}
			
			if (node.getNextNodesSize() != 1) {
				return false;
			}
			
			if (node.getNextNode() != pathList.get(i + 1)) {
				return false;
			}
			
			seen.add(node);			
		}
		return true;
	}

	protected void updateGraph(LinkedList<Node> pathList, Collection<Node> workingSet, Node treeStart) {
		
		Node path = null;
		boolean allNodesNull = true;
		for(Node node : pathList){
			if(!node.isNullNode()){
				allNodesNull = false;
			}
		}
		if(allNodesNull){
			path = new NullNode();
		} else {
			path = new PathNode(pathList);
		}
		
		if (pathList.contains(treeStart)) {
			m_treeStart = path;
		} else {
			m_treeStart = treeStart;
		}
		
		Node startNodeInPath = pathList.getFirst();
		Node endNodeInPath = pathList.getLast(); 
		
		for (Node prev : startNodeInPath.getPrevNodes()) {
			path.addPrevNode(prev);
			
			
			prev.removeNextNode(startNodeInPath);
			//prev.getNextNodes().remove(startNodeInPath);
			prev.addNextNode(path);
		}
		for (Node next : endNodeInPath.getNextNodes()) {
			path.addNextNode(next);
			
			next.removePrevNode(endNodeInPath);
			next.addPrevNode(path);
		}
		
		workingSet.removeAll(pathList);
		workingSet.add(path);
	}

	public boolean usesDominatorTree() {
		return true;
	}
}
