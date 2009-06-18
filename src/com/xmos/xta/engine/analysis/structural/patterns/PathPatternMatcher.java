package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.PathNode;

public class PathPatternMatcher extends PatternMatcher {	
	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		
		m_treeStart = treeStart;
		LinkedList<Node> pathList = new LinkedList<Node>();
		pathList.add(currentNode);
		if (isStart(currentNode)) {
			while (continueDown(currentNode)) {
				currentNode = currentNode.getNextNodes().iterator().next();
				pathList.add(currentNode);
			}
		}
		
		if (pathList.size() > 1) {
			assert(pathSanityCheck(pathList));
			updateGraph(pathList, workingSet, verbose, treeStart);
			return true;
		} else {
			return false;
		}
	}

	private boolean isStart(Node node){
		//im really not over happy about this. will have to verify it
		if(node == m_treeStart){
			return true;
		} else 
			if (node.getPrevNodes().size() ==  0) {
			return true;
		} else if (node.getPrevNodes().size() ==  1) {
			Node prev = node.getPrevNodes().iterator().next();
			if (prev.getNextNodes().size() > 1) {
				return true;
			} else {
				return false;
			}
		} else if (node.getPrevNodes().size() > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isEnd(Node node) {
		if (node.getNextNodes().size() ==  0){
			return true;
		} else if (node.getNextNodes().size() ==  1) {
			Node next = node.getNextNodes().iterator().next();
			if (next.getPrevNodes().size() > 1) {
				return true;
			} else {
				return false;
			}
		} else if (node.getNextNodes().size() > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean continueDown(Node node){
		if (node.getNextNodes().size() == 1 && !isEnd(node) 
				&& !node.getDominators().contains(node.getNextNodes().iterator().next())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean pathSanityCheck(LinkedList<Node> pathList) {
		Set<Node> seen = new HashSet<Node>();
		
		if (pathList.size() < 2) {
			return false;
		}
		
		for (int i = 0; i < pathList.size() - 2; i++) {
			Node node = pathList.get(i);
			
			if (seen.contains(node)) {
				return false;
			}
			
			if (node.getNextNodes().size() != 1) {
				return false;
			}
			
			if (node.getNextNodes().iterator().next() != pathList.get(i + 1)) {
				return false;
			}
			
			seen.add(node);			
		}
		return true;
	}

	protected void updateGraph(LinkedList<Node> pathList, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		PathNode path = new PathNode(pathList);
		
		if (pathList.contains(treeStart)) {
			m_treeStart = path;
		} else {
			m_treeStart = treeStart;
		}
		
		if (verbose){
			System.out.println("#make a new path from " + pathList + " called " + path);
		}
		
		Node startNodeInPath = pathList.getFirst();
		Node endNodeInPath = pathList.getLast(); 
		
		for (Node prev : startNodeInPath.getPrevNodes()) {
			path.addPrevNode(prev);
			prev.getNextNodes().remove(startNodeInPath);
			prev.addNextNode(path);
		}
		for (Node next : endNodeInPath.getNextNodes()) {
			path.addNextNode(next);
			next.getPrevNodes().remove(endNodeInPath);
			next.addPrevNode(path);
		}
		
		workingSet.removeAll(pathList);
		workingSet.add(path);
	}

	public boolean usesDominatorTree() {
		return true;
	}
}
