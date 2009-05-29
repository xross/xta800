package com.xmos.xta.engine.analysis.patterns;

import java.util.Collection;

import com.xmos.xta.engine.analysis.nodes.Node;


public abstract class PatternMatcher {
	protected Node m_treeStart;
	
	public abstract boolean isMatch(Node node, Collection<Node> workingSet, boolean verbose, Node treeStart);
	
	public Node getTreeStart() {
		return m_treeStart;
	}
	
	public abstract boolean usesDominatorTree();
}
