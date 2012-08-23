package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import java.util.Map;

import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status.S;

public class ConditionalNodeAttributes extends NodeAttributes {
	private Map<Node, Long> m_setIterations;
	private Node m_cachedWorstCaseOption;
	private Node m_cachedBestCaseOption;

	public ConditionalNodeAttributes(Node node) {
		super(node);
		m_setIterations = new IteratableMap<Node, Long>();
		m_cachedWorstCaseOption = null;
		m_cachedBestCaseOption = null;
	}

	// --------------------------------------------------------------------- //
	// Statistics Methods
	// --------------------------------------------------------------------- //
	public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getFixedTime() != null) {			
			long totalIterations = getTotalIterations(c, ignoreIterationsAboveCond);
			if (totalIterations == INFINITE_ITERATIONS) {
				status.mergeStatus(S.INFINITE);
				return Double.MAX_VALUE;

			} else if (totalIterations == 0) {
				status.mergeStatus(S.ZERO);
				return 0;
			}
			return getFixedTime().getNs() * totalIterations;
		}

		if (getCachedTimeTakenValid(c) && (ignoreIterationsAboveCond == null)) {
			status.mergeStatus(getCachedTimeTakenStatus(c));
			double timeTaken = getCachedTimeTaken(c);
			return timeTaken;
		}

		double value = 0;
		for (Node child : m_setIterations.keySet()) {
			Status childStatus = new Status();
		    value += child.getAttributes(false).getTimeTaken(c, childStatus, ignoreIterationsAboveCond);
			status.mergeStatus(childStatus.getStatus());
		}

		Node option = getFloatingOption(c, ignoreIterationsAboveCond);
		if (option != null) {
			Status childStatus = new Status();
			value += option.getAttributes(false).getTimeTaken(c, childStatus, ignoreIterationsAboveCond);
			status.mergeStatus(childStatus.getStatus());
		}

		if (ignoreIterationsAboveCond == null) {
			setCachedTimeTaken(c, value);
			setCachedTimeTakenStatus(c, status.getStatus());
			setCachedTimeTakenValid(c, true);
		}
		return value;
	}

	public long getNumCycles(Case c, Status status, Node ignoreIterationsAboveCond) {
    	if (getCachedNumCyclesValid(c) && (ignoreIterationsAboveCond == null)) {
    		status.mergeStatus(getCachedNumCyclesStatus(c));
    		return getCachedNumCycles(c);
    	}

    	long value = 0;
    	for (Node child : m_setIterations.keySet()) {
   		    value += child.getAttributes(false).getNumCycles(c, status, ignoreIterationsAboveCond);
    	}
    	Node option = getFloatingOption(c, ignoreIterationsAboveCond);
    	if (option != null) {
    		value += option.getAttributes(false).getNumCycles(c, status, ignoreIterationsAboveCond);
    	}
		if (ignoreIterationsAboveCond == null) {
			setCachedNumCycles(c, value);
			setCachedNumCyclesStatus(c, status.getStatus());
			setCachedNumCyclesValid(c, true);
		}
		return value;
	}

	public long getNumInstructions(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getCachedNumInstructionsValid(c) && (ignoreIterationsAboveCond == null)) {
			status.mergeStatus(getCachedNumInstructionsStatus(c));
			return getCachedNumInstructions(c);
		}

		long value = 0;
		for (Node child : m_setIterations.keySet()) {
		    value += child.getAttributes(false).getNumInstructions(c, status, ignoreIterationsAboveCond);
		}
		Node option = getFloatingOption(c, ignoreIterationsAboveCond);
		if (option != null) {
			value += option.getAttributes(false).getNumInstructions(c, status, ignoreIterationsAboveCond);
		}
		if (ignoreIterationsAboveCond == null) {
			setCachedNumInstructions(c, value);
			setCachedNumInstructionsStatus(c, status.getStatus());
			setCachedNumInstructionsValid(c, true);
		}
		return value;
	}

	public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveCond) {
		if (getCachedNumFnopsValid(c) && (ignoreIterationsAboveCond == null)) {
			status.mergeStatus(getCachedNumFnopsStatus(c));
			return getCachedNumFnops(c);
		}

		long value = 0;
		for (Node child : m_setIterations.keySet()) {
		    value += child.getAttributes(false).getNumFNops(c, status, ignoreIterationsAboveCond);
		}
		Node option = getFloatingOption(c, ignoreIterationsAboveCond);
		if (option != null) {
			value += option.getAttributes(false).getNumFNops(c, status, ignoreIterationsAboveCond);
		}
		if (ignoreIterationsAboveCond == null) {
			setCachedNumFnops(c, value);
			setCachedNumFnopsStatus(c, status.getStatus());
			setCachedNumFnopsValid(c, true);
		}
		return value;
	}

	public long getNumPaths() throws NumPathsOverflowException {
		long value = 0;
		for (Node child : getNode().iChildren()) {
			value = addNumPaths(value, child.getAttributes(false).getNumPaths());
		}

		return value;
	}

	// --------------------------------------------------------------------- //
	// Loop Iteration Methods
	// --------------------------------------------------------------------- //
	public void loopPathIterationsValid(Node optionNode, long numIterations) throws Exception {
		// Gets the iterations set from above for this node.
		long loopIterations = 1;
		try {
			loopIterations = getNode().getParent().getAttributes(false).getLocalIterations(getNode());

		} catch (CondIterationsNotSetException e) {
			throw new Exception("Within a nested loop, but the outer loop iterations for this path have not been set");

		} catch (InfiniteIterationsException e) {
			throw new Exception("Cannot set path iterations on an infinite loop");
		}

		// Calculates the total iterations already set on all options of
		// this conditional (excluding the contribution from the option we are
		// try to set here)
		long totalNumIterationsSet = getTotalNumIterationsSet();
		if (m_setIterations.get(optionNode) != null)
			totalNumIterationsSet -= m_setIterations.get(optionNode);

		// Check that we will not set an iteration count that
		// overflows the iteration count set on the enclosing loop...
   		long newTotal = totalNumIterationsSet + numIterations;
   		if (newTotal > loopIterations)
   			throw new Exception("Attempting to set a path iteration count that will overflow those set on the enclosing loop");

   		// If there is a remainder, checks that we have at least
   		// one option that hasn't had its iterations set (excluding this one)...
   		long remainder = loopIterations - newTotal;
   		if (remainder > 0) {
   			int numUnsetOptions = getNode().iChildren().size() - m_setIterations.size();
   			if (m_setIterations.get(optionNode) == null)
   				--numUnsetOptions;

   			if (numUnsetOptions == 0) {
				throw new Exception("Attempting to set a path iteration count that will underflow those set on the enclosing loop");
   			}
   		}
	}

	public void setLoopPathIterations(Node optionNode, long numIterations) {
   		m_setIterations.put(optionNode, numIterations);
	}

	public long getLoopPathIterations(long loopIterations, Node optionNode, Case c) {
		if (m_setIterations.get(optionNode) != null) {
			// The iterations have been explicitly set for this option.
			return m_setIterations.get(optionNode);

		} else {
			// Need to assign any remaining iterations to the best/worst case.
			long totalNumIterationsSet = getTotalNumIterationsSet();
			assert(totalNumIterationsSet <= loopIterations);
	   		long remainder = loopIterations - totalNumIterationsSet;
	   		if (remainder > 0) {
				Node option = getFloatingOption(c, getNode());
				if (option == optionNode)
					return remainder;
	   		}
			return 0;
		}
	}

	public long getSetLoopPathIterations(Node optionNode) {
		if (m_setIterations.get(optionNode) != null) {
			// The iterations have been explicitly set for this option.
			return m_setIterations.get(optionNode);
		}
		return 0;
	}

	public boolean getSetOption(Node optionNode) {
		return m_setIterations.get(optionNode) != null;
	}

	public void resetPathIterations() {
		m_setIterations.clear();
	}

	long getLocalIterations(Node child)
        throws CondIterationsNotSetException, InfiniteIterationsException {

		if (m_setIterations.get(child) != null) {
			return m_setIterations.get(child);
		}
		throw new CondIterationsNotSetException();
	}

	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveCond)
	    throws IgnoreCondIterationsException, InfiniteIterationsException {

		if (getNode() == ignoreIterationsAboveCond) {
			throw new IgnoreCondIterationsException();

		} else {
			Iterations iterations = null;
			if (getNode().getParent() != null) {
				iterations =  getNode().getParent().getAttributes(false).getTotalIterations(
						c, getNode(), ignoreIterationsAboveCond);

			    long localCondIterations = getLoopPathIterations(iterations.getLocalIterations(), child, c);
			    iterations.setLocalIterations(localCondIterations);
			    return iterations;
			}
            return new Iterations();
		}
	}

	void invalidateCacheIterations() {
		m_cachedWorstCaseOption = null;
		m_cachedBestCaseOption = null;
	}

	private long getTotalNumIterationsSet() {
		long totalNumIterationsSet = 0;
		for (Node option : m_setIterations.keySet()) {
			totalNumIterationsSet += m_setIterations.get(option);
	    }
  	    return totalNumIterationsSet;
	}

    private Node getFloatingOption(Case c, Node ignoreIterationsAboveCond) {
    	switch (c) {
    	case WORST:
    		if (m_cachedWorstCaseOption == null) {
    			m_cachedWorstCaseOption = getNode().getUnsetOption(c, ignoreIterationsAboveCond);
    		}
			return m_cachedWorstCaseOption;

    	case BEST:
    		if (m_cachedBestCaseOption == null) {
    			m_cachedBestCaseOption = getNode().getUnsetOption(c, ignoreIterationsAboveCond);
    		}
			return m_cachedBestCaseOption;
    	}
    	assert(false);
    	return null;
    }

	public boolean hasFloatingIterations() {
		long loopIterations = 1;
		try {
			loopIterations = getNode().getParent().getAttributes(false).getLocalIterations(getNode());

		} catch (CondIterationsNotSetException e) {
			return true;

		} catch (InfiniteIterationsException e) {
			return false;
		}

		return (loopIterations - getTotalNumIterationsSet()) > 0;
	}
}
