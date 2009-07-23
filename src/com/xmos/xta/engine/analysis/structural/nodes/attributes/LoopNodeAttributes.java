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

	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveSection)
	    throws IgnoreSectionIterationsException, InfiniteIterationsException {

		Iterations iterations = null;
		if (getNode().getParent() != null) {	
            try {			
			    iterations = getNode().getParent().getAttributes().getTotalIterations(
			    		c, getNode(), ignoreIterationsAboveSection);
			    
		    } catch (IgnoreSectionIterationsException e) {
		    	iterations = new Iterations();
		    }
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
