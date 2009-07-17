package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class SelfLoopNodeAttributes extends NodeAttributes {
	private long m_numIterations;
	
	public SelfLoopNodeAttributes(Node node) {
		super(node);
		m_numIterations = 0;
	}

	public boolean loopIterationsValid(long numIterations) {
		if (numIterations > 0) {
			return true;
		}
		return false;
	}
	
	public void setLoopIterations(long numIterations) {
		assert(loopIterationsValid(numIterations));
		m_numIterations = numIterations;
	}
	
	public long getLoopIterations() {
		return m_numIterations;
	}		
	
	long getTotalIterations(Case c, Node child, Node ignoreIterationsAboveSection)
	    throws IgnoreSectionIterationsException, InfiniteIterationsException {

		if (getIsAbsolute())
			return m_numIterations;
		
		long iterations = 1;
		if (getNode().getParent() != null) {
			
		    try {			
				iterations = getNode().getParent().getAttributes().getTotalIterations(c, getNode(), ignoreIterationsAboveSection);
		    } catch (IgnoreSectionIterationsException e) {
		    	iterations = IGNORE_SECTION_ITERATIONS;
		    }			
		}
		return iterations * m_numIterations;		
	}	
}
