package com.xmos.xta.engine.analysis.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.nodes.Section;


public class SectionPatternMatcher extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, boolean verbose, Node treeStart) {
		m_treeStart = treeStart;
		
		// detect all the jump nodes first
		Set<Node> jumpNodes = new HashSet<Node>();
		for (Node nextNode : currentNode.getNextNodes()) {
			// hop backwards
			Set<Node> previous = new HashSet<Node>(nextNode.getPrevNodes());
			for (Node prev : previous) {
				Set<Node> prevSet = new HashSet<Node>(prev.getPrevNodes());
				if (prevSet.contains(currentNode)) {
					jumpNodes.add(nextNode);
				}
			}
		}
		
		Set<Node> endNodes = new HashSet<Node>(jumpNodes);
		List<Node> nextList = new LinkedList<Node>(currentNode.getNextNodes());
		
		for (int i = 0; i <= nextList.size() - 2; i++) {
			for (int j = i + 1; j <= nextList.size() - 1; j++) {
				Node a = nextList.get(i);
				Node b = nextList.get(j);

				Set<Node> intersection = new HashSet<Node>(a.getNextNodes());
				intersection.retainAll(b.getNextNodes());
				endNodes.addAll(intersection);
			}
		}
		
		for (Node endNode : endNodes) {
			if (endNode == currentNode)
				continue;
			Set<Node> sectionNodes = new HashSet<Node>();
			for (Node nextNode : currentNode.getNextNodes()) {
				if (nextNode.getNextNodes().contains(endNode)) {
					if (nextNode.getNextNodes().size() == 1 && nextNode.getPrevNodes().size() == 1) {
						sectionNodes.add(nextNode);
					} else {
						continue;
					}
				}
			}
			if (jumpNodes.contains(endNode)) {
				if (sectionNodes.size() > 0) {
					if (!sectionSanityCheck(currentNode, sectionNodes, endNode, endNode)) {
						System.err.println("#section failed sanity check");
						continue;
					}
					updateGraph(currentNode, sectionNodes, endNode, workingSet, endNode, verbose);
					return true;
				}
			} else {
				if (sectionNodes.size() > 1) {
					if (!sectionSanityCheck(currentNode, sectionNodes, null, endNode)) {
						System.err.println("#section failed sanity check");
						continue;
					}
					updateGraph(currentNode, sectionNodes, null, workingSet, endNode,verbose);
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

	protected void updateGraph(Node startNode, Collection<Node> sectionNodes, Node jumpNode, Collection<Node> workingSet, Node endNode, boolean verbose) {
		Section ms = new Section(sectionNodes, jumpNode);
		
		if(verbose){
			System.out.println("#have found a section from " + startNode + " to " + endNode + " jump: " + (jumpNode != null));
			System.out.println("#have replaced "+sectionNodes + " with " + ms);
		}
		
		startNode.getNextNodes().removeAll(sectionNodes);
		startNode.addNextNode(ms);

		endNode.getPrevNodes().removeAll(sectionNodes);
		endNode.addPrevNode(ms);

		if (jumpNode != null) {
			startNode.getNextNodes().remove(endNode);
			endNode.getPrevNodes().remove(startNode);
		}

		ms.addNextNode(endNode);
		ms.addPrevNode(startNode);

		workingSet.removeAll(sectionNodes);
		workingSet.add(ms);

	}
	
	protected void updateGraph(Node startNode, Collection<Node> sectionNodes, Collection<Node> workingSet,  boolean verbose) {
		Section ms = new Section(sectionNodes, null);
		
		if(verbose){
			System.out.println("#have found a section from; " + startNode + " to: none");
			System.out.println("#have replaced "+sectionNodes + " with " + ms);
		}

		startNode.getNextNodes().removeAll(sectionNodes);
		startNode.addNextNode(ms);

		ms.addPrevNode(startNode);

		workingSet.removeAll(sectionNodes);
		workingSet.add(ms);

	}
	

	public boolean usesDominatorTree() {
		return false;
	}
}
