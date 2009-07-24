package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;

public class SectionPatternMatcher extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart) {
		m_treeStart = treeStart;

		if (currentNode.getNextNodesSize()< 2) {
			return false;
		} 
		
		//detect all the jump nodes first
		Set<Node> jumpNodes = new TreeSet<Node>();
		for(Node nextNode : currentNode.getNextNodes()){
			//hop backwards
			Set<Node> previous = new TreeSet<Node>(nextNode.getPrevNodes());
			previous.remove(currentNode);
			previous.remove(nextNode);
			for(Node prev : previous){
				Set<Node> prevSet = new TreeSet<Node>(prev.getPrevNodes());
				if(prevSet.contains(currentNode)){
					jumpNodes.add(nextNode);
				}
			}
		}
		
		Set<Node> endNodes = new TreeSet<Node>(jumpNodes);
		
		List<Node> nextList = new LinkedList<Node>(currentNode.getNextNodes());
		
		for (int i = 0; i <= nextList.size() - 2; i++) {
			Set<Node> intersection = new TreeSet<Node>();
			for (int j = i + 1; j <= nextList.size() - 1; j++) {
				Node a = nextList.get(i);
				Node b = nextList.get(j);
				intersection.clear();
				intersection.addAll(a.getNextNodes());
				intersection.retainAll(b.getNextNodes());
				endNodes.addAll(intersection);
			}
		}
		
		for (Node endNode : endNodes) {
			if (endNode == currentNode)
				continue;
			Set<Node> sectionNodes = new TreeSet<Node>();
			for (Node nextNode : currentNode.getNextNodes()) {
				if (nextNode.getNextNodes().contains(endNode)) {
					if (nextNode.getNextNodesSize() == 1 && nextNode.getPrevNodes().size() == 1) {
						sectionNodes.add(nextNode);
					} else {
						continue;
					}
				}
			}
			
			if (jumpNodes.contains(endNode)) {
				if (sectionNodes.size() > 0) {
					
					//if something goes wrong this is probably to blame
					if(endNode.getNextNodesSize() == 1 && endNode.getNextNode() == currentNode && currentNode != m_treeStart){
						continue;
					}
					if (!sectionSanityCheck(currentNode, sectionNodes, endNode, endNode)) {
						System.err.println("#section failed sanity check");
						continue;
					}
					updateGraph(currentNode, sectionNodes, true, workingSet, endNode);
					return true;
				}
			} else {
				if (sectionNodes.size() > 1) {

					assert(sectionSanityCheck(currentNode, sectionNodes, null, endNode));
					updateGraph(currentNode, sectionNodes, false, workingSet, endNode);
					return true;
				}
			}
		}
		return false;
	}

	private boolean sectionSanityCheck(Node startNode, Collection<Node> sectionNodes, Node jumpNode, Node endNode) {

		if (!startNode.getNextNodes().containsAll(sectionNodes)) {
			return false;
		}

		int edgeCount = sectionNodes.size();
		if (jumpNode != null) {
			edgeCount++;
		}
		if (edgeCount < 2) {
			return false;
		}

		if (!endNode.getPrevNodes().containsAll(sectionNodes)) {
			return false;
		}

		if (jumpNode != null) {
			if (jumpNode != endNode) {
				return false;
			}
		}
		return true;
	}

	protected void updateGraph(Node startNode, Collection<Node> sectionNodes, boolean isOptional, Collection<Node> workingSet, Node endNode) {
		SectionNode ms = new SectionNode(sectionNodes, isOptional);
		
		for(Node sectionNode : sectionNodes){
			startNode.removeNextNode(sectionNode);
		}
		
		//startNode.getNextNodes().removeAll(sectionNodes);
		startNode.addNextNode(ms);
		
		endNode.getPrevNodes().removeAll(sectionNodes);
		endNode.addPrevNode(ms);

		if (isOptional) {
			
			startNode.removeNextNode(endNode);
			//startNode.getNextNodes().remove(endNode);
			endNode.getPrevNodes().remove(startNode);
		}

		ms.addNextNode(endNode);
		ms.addPrevNode(startNode);

		workingSet.removeAll(sectionNodes);
		workingSet.add(ms);
	}
	
	public boolean usesDominatorTree() {
		return false;
	}
}
