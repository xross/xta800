package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public abstract class PatternMatcher {
	protected Node m_treeStart;

	public abstract boolean usesDominatorTree();
	public abstract boolean isMatch(Node node, Collection<Node> workingSet, Node treeStart, Aborter aborter ) throws AbortedException;

	public Node getTreeStart() {
		return m_treeStart;
	}
}
