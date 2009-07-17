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

	long getTotalIterations(Case c, Node child, Node ignoreIterationsAboveSection)
	    throws IgnoreSectionIterationsException, InfiniteIterationsException {

		long iterations = 1;
		if (getNode().getParent() != null) {
			
            try {			
			    iterations = getNode().getParent().getAttributes().getTotalIterations(c, getNode(), ignoreIterationsAboveSection);
		    } catch (IgnoreSectionIterationsException e) {
		    	iterations = IGNORE_SECTION_ITERATIONS;
		    }
		}
		
		if (getIsAbsolute()) {
			long headerAdjustment = child.getAttributes().getIsLoopHeader() ? iterations : 0;									
			return m_numIterations + headerAdjustment;
		}
		
		long headerAdjustment = child.getAttributes().getIsLoopHeader() ? 1 : 0;						
		return iterations * (m_numIterations + headerAdjustment);		
	}	
}
