package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class LoopNodeAttributes extends NodeAttributes {
	private long m_numIterations;
	
	public LoopNodeAttributes(Node node) {
		super(node);
		m_numIterations = 0;
	}

	public boolean loopIterationsValid(long numIterations) {
		return true;
	}
	
	public void setLoopIterations(long numIterations) {
		m_numIterations = numIterations;
	}
	
	public long getLoopIterations() {
		return m_numIterations;
	}

	long getNodeIterations(Case c, Node child, Node ignoreIterationsAboveSection) throws IgnoreSectionIterationsException {
		return m_numIterations;		
	}	
}
