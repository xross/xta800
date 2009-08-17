package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class LoopNodeAttributes extends NodeAttributes {
	private long m_numIterations;
	
	public LoopNodeAttributes(Node node) {
		super(node);
		m_numIterations = 0;
	}

	public void loopIterationsValid(long numIterations) throws Exception {
	}
	
	public void setLoopIterations(long numIterations) {
		m_numIterations = numIterations;
	}

	public long getLoopIterations() {
		return m_numIterations;
	}

	long getLocalIterations(Node child) 
        throws CondIterationsNotSetException, InfiniteIterationsException {
	
		if (getIsAbsolute()) {	
			long iterations = 1;
			if (getNode().getParent() != null) {
				getNode().getParent().getAttributes().getLocalIterations(getNode());
			}
			long headerAdjustment = child.getAttributes().getIsLoopHeader() ? iterations : 0;
			return m_numIterations + headerAdjustment;
		}
		
		long headerAdjustment = child.getAttributes().getIsLoopHeader() ? 1 : 0;						
		return m_numIterations + headerAdjustment;		
    }
	
	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveCond)
	    throws IgnoreCondIterationsException, InfiniteIterationsException {

		Iterations iterations = null;
		if (getNode().getParent() != null) {	
            try {			
			    iterations = getNode().getParent().getAttributes().getTotalIterations(
			    		c, getNode(), ignoreIterationsAboveCond);
			    
		    } catch (IgnoreCondIterationsException e) {
		    	iterations = new Iterations();
		    }
		} else {
			return new Iterations();
		}
		
		long iterationsAbove = iterations.getGlobalIterations() * iterations.getLocalIterations();
				
		if (getIsAbsolute()) {			
			long headerAdjustment = child.getAttributes().getIsLoopHeader() ? iterationsAbove : 0;
			iterations.setGlobalIterations(1);
			iterations.setLocalIterations(m_numIterations + headerAdjustment);
			return iterations;
		}
		
		long headerAdjustment = child.getAttributes().getIsLoopHeader() ? 1 : 0;						
		iterations.setGlobalIterations(iterationsAbove);
		iterations.setLocalIterations(m_numIterations + headerAdjustment);
		return iterations;		
	}	
}
