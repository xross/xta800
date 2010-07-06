package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public abstract class PatternMatcher {
	protected CfgNode m_treeStart;

	public abstract boolean usesDominatorTree();
	public abstract boolean isMatch(CfgNode node, Collection<CfgNode> workingSet, CfgNode treeStart);
	
	public CfgNode getTreeStart() {
		return m_treeStart;
	}
	
	Node getStructuralNode(CfgNode cfgNode) {
		assert(cfgNode instanceof CfgContainerNode);			
		return ((CfgContainerNode)cfgNode).getNode();
	}

	List<Node> getStructuralNodes(Collection<CfgNode> cfgNodes) {
		List<Node> structuralNodes = new ArrayList<Node>();		
		for (CfgNode cfgNode : cfgNodes) {
			structuralNodes.add(getStructuralNode(cfgNode));
		}
        return structuralNodes;
	}
}
