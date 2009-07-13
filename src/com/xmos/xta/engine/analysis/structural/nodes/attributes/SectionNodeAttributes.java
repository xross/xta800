package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class SectionNodeAttributes extends NodeAttributes {
	private Map<Node, Long> m_setIterations;
	
	public SectionNodeAttributes(Node node) {
		super(node);	
		m_setIterations = new HashMap<Node, Long>();
	}

	// --------------------------------------------------------------------- //
	// Statistics Methods
	// --------------------------------------------------------------------- //	
	public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getCachedTimeTakenValid(c) && (ignoreIterationsAboveSection == null)) {
			status.setStatus(getCachedTimeTakenStatus(c));		
			return getCachedTimeTaken(c);
		}
		
		double value = 0;
		for (Node child : m_setIterations.keySet()) {
		    value += child.getAttributes().getTimeTaken(c, status, ignoreIterationsAboveSection);
		}
		Node option = getNode().getUnsetOption(c, ignoreIterationsAboveSection);
		if (option != null) {
			value += option.getAttributes().getTimeTaken(c, status, ignoreIterationsAboveSection); 
		}		
		if (ignoreIterationsAboveSection == null) {			
			setCachedTimeTaken(c, value);
			setCachedTimeTakenStatus(c, status.getStatus());			
			setCachedTimeTakenValid(c, true);
		}
		return value;			
	}

	public long getNumCycles(Case c, Status status, Node ignoreIterationsAboveSection) {
    	if (getCachedNumCyclesValid(c) && (ignoreIterationsAboveSection == null)) {
    		status.setStatus(getCachedNumCyclesStatus(c));    	
    		return getCachedNumCycles(c);
    	}
    		
    	long value = 0;
    	for (Node child : m_setIterations.keySet()) {
   		    value += child.getAttributes().getNumCycles(c, status, ignoreIterationsAboveSection);
    	}		
    	Node option = getNode().getUnsetOption(c, ignoreIterationsAboveSection);
    	if (option != null) {
    		value += option.getAttributes().getNumCycles(c, status, ignoreIterationsAboveSection); 
    	}    		
		if (ignoreIterationsAboveSection == null) {
			setCachedNumCycles(c, value);
			setCachedNumCyclesStatus(c, status.getStatus());			
			setCachedNumCyclesValid(c, true);				
		}
		return value;
	}

	public long getNumInstructions(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getCachedNumInstructionsValid(c) && (ignoreIterationsAboveSection == null)) {
			status.setStatus(getCachedNumInstructionsStatus(c));
			return getCachedNumInstructions(c);
		}
		
		long value = 0;
		for (Node child : m_setIterations.keySet()) {
		    value += child.getAttributes().getNumInstructions(c, status, ignoreIterationsAboveSection);
		}		
		Node option = getNode().getUnsetOption(c, ignoreIterationsAboveSection);
		if (option != null) {
			value += option.getAttributes().getNumInstructions(c, status, ignoreIterationsAboveSection); 
		}
		if (ignoreIterationsAboveSection == null) {
			setCachedNumInstructions(c, value);
			setCachedNumInstructionsStatus(c, status.getStatus());			
			setCachedNumInstructionsValid(c, true);				
		}
		return value;
	}

	public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveSection) {
		if (getCachedNumFnopsValid(c) && (ignoreIterationsAboveSection == null)) {
			status.setStatus(getCachedNumFnopsStatus(c));
			return getCachedNumFnops(c);
		}
		
		long value = 0;
		for (Node child : m_setIterations.keySet()) {
		    value += child.getAttributes().getNumFNops(c, status, ignoreIterationsAboveSection);
		}		
		Node option = getNode().getUnsetOption(c, ignoreIterationsAboveSection);
		if (option != null) {
			value += option.getAttributes().getNumFNops(c, status, ignoreIterationsAboveSection); 
		}			
		if (ignoreIterationsAboveSection == null) {
			setCachedNumFnops(c, value);
			setCachedNumFnopsStatus(c, status.getStatus());			
			setCachedNumFnopsValid(c, true);				
		}
		return value;
	}

	public long getNumPaths() {
		long value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getNumPaths();
		}
		return value;
	}
	
	// --------------------------------------------------------------------- //
	// Loop Iteration Methods
	// --------------------------------------------------------------------- //
	public boolean loopPathIterationsValid(Node loopNode, Node optionNode, long numIterations) {
		long loopIterations = loopNode.getAttributes().getLoopIterations();

		// Calculates the total iterations already set on all options of 
		// this section (excluding the contribution from the option we are
		// try to set here)
		long totalNumIterationsSet = getTotalNumIterationsSet();
		if (m_setIterations.get(optionNode) != null)
			totalNumIterationsSet -= m_setIterations.get(optionNode);
		
		// Check that we will not set an iteration count that
		// overflows the iteration count set on the enclosing loop...
   		long newTotal = totalNumIterationsSet + numIterations;
   		if (newTotal > loopIterations)
   			return false;

   		// If there is a remainder, checks that we have at least 
   		// one option that hasn't had its iterations set (excluding this one)...
   		long remainder = loopIterations - newTotal;
   		if (remainder > 0) {
   			int numUnsetOptions = getNode().getChildren().size() - m_setIterations.size();
   			if (m_setIterations.get(optionNode) == null)
   				--numUnsetOptions;

   			if (numUnsetOptions == 0) {
   				if (!getNode().isSectionOptional()) {
   	   	   			return false;   					
   				}
   			}
   		}

   		// Sets the iterations on the given section option node..
   		m_setIterations.put(optionNode, numIterations);
		return true;
	}	
	
	public void setLoopPathIterations(Node loopNode, Node optionNode, long numIterations) {
		assert(loopPathIterationsValid(loopNode, optionNode, numIterations));
   		m_setIterations.put(optionNode, numIterations);
	}
	
	public long getLoopPathIterations(long loopIterations, Node optionNode, Case c) {
		if (m_setIterations.get(optionNode) != null) {
			// The iterations have been explicitly set for this option.
			return m_setIterations.get(optionNode);	
			
		} else {
			// Need to assign any remaining iterations to the best/worst case.
			long totalNumIterationsSet = getTotalNumIterationsSet();
	   		long remainder = loopIterations - totalNumIterationsSet;
	   		if (remainder > 0) {
				Node option = getNode().getUnsetOption(c, getNode());
				if (option == optionNode)
					return remainder;
	   		}			
			return 0;			
		}
	}
	
	public boolean getSetOption(Node optionNode) {
		return m_setIterations.get(optionNode) != null;
	}
	
	public void resetPathIterations() {
		m_setIterations.clear();
	}
				
	private long getTotalNumIterationsSet() {
		long totalNumIterationsSet = 0;
		for (Node option : m_setIterations.keySet()) {
			totalNumIterationsSet += m_setIterations.get(option);
	    }
  	    return totalNumIterationsSet;
	}
}
