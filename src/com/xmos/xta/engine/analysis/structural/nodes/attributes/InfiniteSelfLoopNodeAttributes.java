package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class InfiniteSelfLoopNodeAttributes extends NodeAttributes {
	private long m_numIterations;
	
	public InfiniteSelfLoopNodeAttributes(Node node) {
		super(node);
	}

	public boolean setLoopIterations(long numIterations) {
		if (numIterations > 0) {
			m_numIterations = numIterations;
			return true;
		}
		return false;
	}
	
	public long getLoopIterations() {
		return m_numIterations;
	}		
}