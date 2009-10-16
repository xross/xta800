package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;

public class SelfLoopNodeAttributes extends NodeAttributes {
	private long m_numIterations;
	private boolean m_sourceLevelIterations;
	
	public SelfLoopNodeAttributes(Node node) {
		super(node);
		m_numIterations = 1;
		m_sourceLevelIterations = false;
	}

	public void loopIterationsValid(long numIterations) throws Exception {
		if (numIterations < 1) {
			throw new Exception("Cannot set iteration count less than 1 on a self loop");
		}
	}
	
	public void setLoopIterations(CommandSource src, Engine engine,
			long numIterations, boolean sourceLevelIterations) {
		
		if (m_sourceLevelIterations) {
			engine.notify(src, Notifier.NOTIFY_WARNING,
				String.format("Overriding loop iterations already set at source level (loop starting at 0x%x)",
					getNode().getStartPc()));
		}
		
		m_sourceLevelIterations = sourceLevelIterations;
		m_numIterations = numIterations;
	}
	
	public long getLoopIterations() {
		return m_numIterations;
	}		
	
	public boolean isSourceLevelIterations() {
		return m_sourceLevelIterations;
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
