package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class BlockNodeAttributes extends NodeAttributes {
	public BlockNodeAttributes(Node node) {
		super(node);
	}

	public double getTimeTaken(Case c, boolean ignoreIterations) {
		if (ignoreIterations) {
			return super.getTimeTaken(c, ignoreIterations);
		}
		return super.getTimeTaken(c, ignoreIterations) * getTotalIterations(c);
	}

	public long getNumCycles(Case c, boolean ignoreIterations) {
		if (ignoreIterations) {
			return super.getNumCycles(c, ignoreIterations);
		}
		return super.getNumCycles(c, ignoreIterations) * getTotalIterations(c);
	}

	public long getNumInstructions(Case c, boolean ignoreIterations) {
		if (ignoreIterations) {
			return super.getNumInstructions(c, ignoreIterations);
		}
		return super.getNumInstructions(c, ignoreIterations) * getTotalIterations(c);
	}
	
	public long getNumFNops(Case c, boolean ignoreIterations) {
		if (ignoreIterations) {
			return super.getNumFNops(c, ignoreIterations);
		}
		return super.getNumFNops(c, ignoreIterations) * getTotalIterations(c);
	}	
}
