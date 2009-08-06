package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.ConditionalNode;

public class ConditionalPatternMatcher extends PatternMatcher {
	
	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart) {
		m_treeStart = treeStart;

		if (currentNode.getNextNodesSize() < 2) {
			return false;
		} 
		
		Map<Node, Set<Node>> condNodeMap = new TreeMap<Node, Set<Node>>();
		Set<Node> isOptionalSet = new HashSet<Node>();
		for (Node node : currentNode.getNextNodes()) {
			Set<Node> nodes = getConditionalDepth(node, 0, currentNode, condNodeMap, isOptionalSet);
			if (nodes == null) {
				return false;
			}
		}
		
		for (Node endNode : condNodeMap.keySet()) {
			boolean isOptional = isOptionalSet.contains(endNode);
			Set<Node> condNodes = condNodeMap.get(endNode);
			int numNodes = condNodes.size();
			if (isOptional) {
				numNodes += 1;
			}
			if (numNodes > 1) {
				assert(condSanityCheck(currentNode, condNodes, isOptional ? endNode : null, endNode));
				updateGraph(currentNode, condNodes, isOptional, workingSet, endNode);
				return true;
			}
		}
		return false;
	}

	private Set<Node> getConditionalDepth(Node currentNode, int depth, Node startNode,
			Map<Node,Set<Node>> endNodeMap, Set<Node> isOptionalSet) {
		
		if ((depth > 1) || (currentNode == startNode)) {
			return null;
			
		} else if (currentNode.getPrevNodesSize() > 1) {
			Set<Node> condNodes = endNodeMap.get(currentNode);
			if (condNodes == null) {
				condNodes = new HashSet<Node>();
				endNodeMap.put(currentNode, condNodes);
			}
			if (depth == 0) {
				isOptionalSet.add(currentNode);
			}
			return condNodes;
			
		} else if (currentNode.getNextNodesSize() == 0) {
			return new HashSet<Node>();
			
		} else if (currentNode.getNextNodesSize() != 1) {
			return null;
			
		} else {
			Set<Node> condNodes = getConditionalDepth(currentNode.getNextNode(), depth + 1, startNode,
					endNodeMap, isOptionalSet);
			
			if (condNodes != null) {
				condNodes.add(currentNode);
			}
			return condNodes;
			
		}
	}

	private boolean condSanityCheck(Node startNode, Collection<Node> condNodes, Node jumpNode, Node endNode) {

		if (!startNode.getNextNodes().containsAll(condNodes)) {
			return false;
		}

		int edgeCount = condNodes.size();
		if (jumpNode != null) {
			edgeCount++;
		}
		
		if (edgeCount < 2) {
			return false;
		}
		
		if (!endNode.getPrevNodes().containsAll(condNodes)) {
			return false;
		}
		
		if (jumpNode != null && jumpNode != endNode) {
			return false;
		}
		return true;
	}

	protected void updateGraph(Node startNode, Collection<Node> condNodes, boolean isOptional,
			Collection<Node> workingSet, Node endNode) {
		
		for (Node condNode : condNodes){
			startNode.removeNextNode(condNode);
		}
		
		ConditionalNode newCondNode = new ConditionalNode(condNodes, isOptional);
		startNode.addNextNode(newCondNode);
		
		endNode.getPrevNodes().removeAll(condNodes);
		endNode.addPrevNode(newCondNode);

		if (isOptional) {
			startNode.removeNextNode(endNode);
			endNode.getPrevNodes().remove(startNode);
		}

		newCondNode.addNextNode(endNode);
		newCondNode.addPrevNode(startNode);

		workingSet.removeAll(condNodes);
		workingSet.add(newCondNode);
	}
	
	public boolean usesDominatorTree() {
		return false;
	}
}
