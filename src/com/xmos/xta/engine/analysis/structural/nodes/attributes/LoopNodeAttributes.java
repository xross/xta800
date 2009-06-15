package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class LoopNodeAttributes extends NodeAttributes {
	public LoopNodeAttributes(Node node) {
		super(node);
	}
	
	public long getCummulativeIterations(Case c) {
		if (getIsAbsolute()) {
			return getNumIterations(c);
		}
		Node parent = getNode().getParent();
		assert(parent != null);
		return parent.getAttributes().getCummulativeIterations(c) * getNumIterations(c);
	}	
}
