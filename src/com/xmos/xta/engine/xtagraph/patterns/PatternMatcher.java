package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Set;

import com.xmos.xta.engine.xtagraph.nodes.Node;

public abstract class PatternMatcher {

	public abstract boolean isMatch(Node node, Set<Node> workingSet);
	
}
