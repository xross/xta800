package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class SelfLoopNodeAttributes extends NodeAttributes {
	private long m_numIterations;
	
	public SelfLoopNodeAttributes(Node node) {
		super(node);
	}

	public void setLoopIterations(long numIterations) {
		m_numIterations = numIterations;
	}
	
	public long getLoopIterations() {
		return m_numIterations;
	}		
}
