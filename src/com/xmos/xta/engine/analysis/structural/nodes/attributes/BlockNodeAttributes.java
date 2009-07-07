package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status.S;

public class BlockNodeAttributes extends NodeAttributes {
	public BlockNodeAttributes(Node node) {
		super(node);
	}

	public double getTimeTaken(Case c, Status status, boolean ignoreIterations) {
		if (ignoreIterations) {
			return super.getTimeTaken(c, status, ignoreIterations);
		}
		long totalIterations = getTotalIterations(c);
		if (totalIterations == INFINITE_ITERATIONS) {
			status.setStatus(S.INFINITE);
			return Double.MAX_VALUE;
		}
	    return super.getTimeTaken(c, status, ignoreIterations) * totalIterations;
	}

	public long getNumCycles(Case c, Status status, boolean ignoreIterations) {
		if (ignoreIterations) {
			return super.getNumCycles(c, status, ignoreIterations);
		}
		long totalIterations = getTotalIterations(c);
		if (totalIterations == INFINITE_ITERATIONS) {
			status.setStatus(S.INFINITE);
			return Long.MAX_VALUE;
		}
		return super.getNumCycles(c, status, ignoreIterations) * totalIterations;
	}

	public long getNumInstructions(Case c, Status status, boolean ignoreIterations) {
		if (ignoreIterations) {
			return super.getNumInstructions(c, status, ignoreIterations);
		}
		long totalIterations = getTotalIterations(c);
		if (totalIterations == INFINITE_ITERATIONS) {
			status.setStatus(S.INFINITE);
			return Long.MAX_VALUE;
		}
		return super.getNumInstructions(c, status, ignoreIterations) * totalIterations;
	}
	
	public long getNumFNops(Case c, Status status, boolean ignoreIterations) {
		if (ignoreIterations) {
			return super.getNumFNops(c, status, ignoreIterations);
		}
		long totalIterations = getTotalIterations(c);
		if (totalIterations == INFINITE_ITERATIONS) {
			status.setStatus(S.INFINITE);
			return Long.MAX_VALUE;
		}
		return super.getNumFNops(c, status, ignoreIterations) * totalIterations;
	}	
}
