package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class SelfLoopNodeAttributes extends NodeAttributes {
	private long m_numIterations;
	
	public SelfLoopNodeAttributes(Node node) {
		super(node);
		m_numIterations = 0;
	}

	public void loopIterationsValid(long numIterations) throws Exception {
		if (numIterations < 1) {
			throw new Exception("Cannot set iteration count less than 1 on a self loop");
		}
	}
	
	public void setLoopIterations(long numIterations) {
		m_numIterations = numIterations;
	}
	
	public long getLoopIterations() {
		return m_numIterations;
	}		
	
	long getLocalIterations(Node child) 
        throws CondIterationsNotSetException, InfiniteIterationsException {
		return m_numIterations;		
	}
	
	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveCond)
	    throws IgnoreCondIterationsException, InfiniteIterationsException {
		
		if (getIsAbsolute()) {
			Iterations iterations = new Iterations();
			iterations.setLocalIterations(m_numIterations);
			return iterations;
		}
		
		if (getNode().getParent() != null) {
			Iterations iterations = null;
		    try {			
		    	iterations = getNode().getParent().getAttributes().getTotalIterations(
						c, getNode(), ignoreIterationsAboveCond);
				
		    } catch (IgnoreCondIterationsException e) {
		    	iterations = new Iterations();
		    }
			iterations.setGlobalIterations(iterations.getGlobalIterations() * iterations.getLocalIterations());
			iterations.setLocalIterations(m_numIterations);
			return iterations;				    
		}
		return new Iterations();
	}	
}
