package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;

import com.xmos.xta.engine.xtagraph.nodes.Node;

public class FunctionPatternMatcher extends PatternMatcher {

	public boolean isMatch(Node node, Collection<Node> workingSet) {
		return false;
	}

}
