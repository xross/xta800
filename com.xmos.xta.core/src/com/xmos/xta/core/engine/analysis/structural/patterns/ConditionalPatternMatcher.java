package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.ConditionalNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class ConditionalPatternMatcher extends PatternMatcher {

    public ConditionalPatternMatcher(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }
    
	public boolean isMatch(CfgNode currentNode, 
	        Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {
	    
		m_treeStart = treeStart;

		if (currentNode.getNextNodesSize() < 2) {
			return false;
		}

		Map<CfgNode, Set<CfgNode>> condNodeMap = new IteratableMap<CfgNode, Set<CfgNode>>();
		Set<CfgNode> isOptionalSet = new NonIteratableSet<CfgNode>();
		for (CfgNode node : currentNode.getNextNodes()) {
			Set<CfgNode> nodes = getConditionalDepth(node, 0, currentNode, condNodeMap, isOptionalSet);
			if (nodes == null) {
				return false;
			}
		}

		for (CfgNode endNode : condNodeMap.keySet()) {
			boolean isOptional = isOptionalSet.contains(endNode);
			Set<CfgNode> condNodes = condNodeMap.get(endNode);
			int numNodes = condNodes.size();
			
			if(condNodes.contains(treeStart)){
				continue;
			}
			if (isOptional) {
				numNodes += 1;
			}
			if (numNodes > 1) {
				updateGraph(currentNode, condNodes, isOptional, workingSet, endNode);
				return true;
			}
		}
		return false;
	}

	private Set<CfgNode> getConditionalDepth(CfgNode currentNode, int depth, CfgNode startNode,
			Map<CfgNode, Set<CfgNode>> endNodeMap, Set<CfgNode> isOptionalSet) {

		if ((depth > 1) || (currentNode == startNode)) {
			return null;

		} else if (currentNode.getPrevNodes().size() > 1) {
			Set<CfgNode> condNodes = endNodeMap.get(currentNode);
			if (condNodes == null) {
				condNodes = new IteratableSet<CfgNode>();
				endNodeMap.put(currentNode, condNodes);
			}
			if (depth == 0) {
				isOptionalSet.add(currentNode);
			}
			return condNodes;

		} else if (currentNode.getNextNodesSize() == 0) {
			return new NonIteratableSet<CfgNode>();

		} else if (currentNode.getNextNodesSize() != 1) {
			return null;

		} else {
			Set<CfgNode> condNodes = getConditionalDepth(currentNode.getNextNode(), 
					depth + 1, startNode, endNodeMap, isOptionalSet);

			if (condNodes != null) {
				condNodes.add(currentNode);
			}
			return condNodes;
		}
	}

	protected void updateGraph(CfgNode startNode, Collection<CfgNode> condNodes, boolean isOptional,
			Collection<CfgNode> workingSet, CfgNode endNode) {

		for (CfgNode condNode : condNodes){
			startNode.removeNextNode(condNode);
		}

		CfgNode newCondNode = createStructuralNode(condNodes, isOptional);		
		
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

	private CfgNode createStructuralNode(Collection<CfgNode> condNodes, boolean isOptional) {		
		Collection<Node> strCondNodes = getStructuralNodes(condNodes);
		Node structuralNode = new ConditionalNode(strCondNodes, isOptional);
		return new CfgContainerNode(structuralNode);		
	}
}
