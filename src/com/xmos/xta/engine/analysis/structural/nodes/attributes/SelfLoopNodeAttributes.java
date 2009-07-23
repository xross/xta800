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
	
	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveSection)
	    throws IgnoreSectionIterationsException, InfiniteIterationsException {
		
		if (getIsAbsolute()) {
			Iterations iterations = new Iterations();
			iterations.setLocalIterations(m_numIterations);
			return iterations;
		}
		
		Iterations iterations = null;
		if (getNode().getParent() != null) {
		    try {			
		    	iterations = getNode().getParent().getAttributes().getTotalIterations(
						c, getNode(), ignoreIterationsAboveSection);
				
		    } catch (IgnoreSectionIterationsException e) {
		    	iterations = new Iterations();
		    }
		}
		iterations.setGlobalIterations(iterations.getGlobalIterations() * iterations.getLocalIterations());
		iterations.setLocalIterations(m_numIterations);
		return iterations;		
	}	
}
