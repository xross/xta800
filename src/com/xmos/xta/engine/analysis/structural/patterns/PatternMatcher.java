package com.xmos.xta.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public abstract class PatternMatcher {
	protected Node m_treeStart;
	
	public abstract boolean usesDominatorTree();
	public abstract boolean isMatch(Node node, Collection<Node> workingSet, Node treeStart);
	
	public Node getTreeStart() {
		return m_treeStart;
	}	
}
