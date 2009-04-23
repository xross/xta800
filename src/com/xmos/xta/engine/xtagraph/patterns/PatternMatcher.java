package com.xmos.xta.engine.xtagraph.patterns;

import java.util.Collection;
import com.xmos.xta.engine.xtagraph.nodes.Node;

public abstract class PatternMatcher {

	public abstract boolean isMatch(Node node, Collection<Node> workingSet);
	
}
