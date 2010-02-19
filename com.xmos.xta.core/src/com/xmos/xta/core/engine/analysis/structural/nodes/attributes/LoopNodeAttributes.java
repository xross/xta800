package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;

public class LoopNodeAttributes extends NodeAttributes {
	private long m_numIterations;
	private boolean m_sourceLevelIterations;

	public LoopNodeAttributes(Node node) {
		super(node);
		m_numIterations = 0;
		m_sourceLevelIterations = false;
	}

	public void loopIterationsValid(long numIterations) throws Exception {
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
