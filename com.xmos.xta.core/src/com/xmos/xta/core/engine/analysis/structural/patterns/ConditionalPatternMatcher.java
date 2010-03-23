package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.ConditionalNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class ConditionalPatternMatcher extends PatternMatcher {

	public boolean isMatch(Node currentNode, Collection<Node> workingSet, Node treeStart, Aborter aborter ) throws AbortedException {
		m_treeStart = treeStart;

		if (currentNode.getNextNodesSize() < 2) {
			return false;
		}

		Map<Node, Set<Node>> condNodeMap = new IteratableMap<Node, Set<Node>>();
		Set<Node> isOptionalSet = new NonIteratableSet<Node>();
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
			
			if(condNodes.contains(treeStart)){
				continue;
			}
			if (isOptional) {
				numNodes += 1;
			}
			if (numNodes > 1) {
				//assert(condSanityCheck(currentNode, condNodes, isOptional ? endNode : null, endNode));
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

		} else if (currentNode.getPrevNodes().size() > 1) {
			Set<Node> condNodes = endNodeMap.get(currentNode);
			if (condNodes == null) {
				condNodes = new IteratableSet<Node>();
				endNodeMap.put(currentNode, condNodes);
			}
			if (depth == 0) {
				isOptionalSet.add(currentNode);
			}
			return condNodes;

		} else if (currentNode.getNextNodesSize() == 0) {
			return new NonIteratableSet<Node>();

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
