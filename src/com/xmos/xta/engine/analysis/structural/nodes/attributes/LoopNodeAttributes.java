package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class LoopNodeAttributes extends NodeAttributes {
	private long m_numIterations;
	
	public LoopNodeAttributes(Node node) {
		super(node);
		m_numIterations = 0;
	}
	
	public boolean setLoopIterations(long numIterations) {
		m_numIterations = numIterations;
		return true;
	}
	
	public long getLoopIterations() {
		return m_numIterations;
	}
}
