package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class SectionNodeAttributes extends NodeAttributes {
	private Map<Node, Long> m_numIterations;
	private Map<Node, Boolean> m_setOptions;
	
	public SectionNodeAttributes(Node node) {
		super(node);	
		initialiseNumIterations();
		initialiseSetOptions();
	}

	// --------------------------------------------------------------------- //
	// Statistics Methods
	// --------------------------------------------------------------------- //	
	public double getTimeTaken(Case c, boolean ignoreIterations) {
		if (!getCachedTimeTakenValid(c) && !ignoreIterations) {
			double value = 0;
			for (Node child : getNode().getChildren()) {
				if (m_setOptions.get(child))
				    value += child.getAttributes().getTimeTaken(c, ignoreIterations);
			}
			Node option = getNode().getUnsetOption(c, ignoreIterations);
			if (option != null) {
				value += option.getAttributes().getTimeTaken(c, ignoreIterations); 
			}			
			setCachedTimeTaken(c, value);
			setCachedTimeTakenValid(c, true);
		}
		return getCachedTimeTaken(c);			
	}

	public long getNumCycles(Case c, boolean ignoreIterations) {
    	if (!getCachedNumCyclesValid(c) && !ignoreIterations) {
    		long value = 0;
    		for (Node child : getNode().getChildren()) {
    			if (m_setOptions.get(child))
    			    value += child.getAttributes().getNumCycles(c, ignoreIterations);
    		}		
    		Node option = getNode().getUnsetOption(c, ignoreIterations);
    		if (option != null) {
    			value += option.getAttributes().getNumCycles(c, ignoreIterations); 
    		}    		
			setCachedNumCycles(c, value);
			setCachedNumCyclesValid(c, true);
		}
		return getCachedNumCycles(c);
	}

	public long getNumInstructions(Case c, boolean ignoreIterations) {
		if (!getCachedNumInstructionsValid(c) && !ignoreIterations) {
			long value = 0;
			for (Node child : getNode().getChildren()) {
				if (m_setOptions.get(child))
				    value += child.getAttributes().getNumInstructions(c, ignoreIterations);
			}		
			Node option = getNode().getUnsetOption(c, ignoreIterations);
			if (option != null) {
				value += option.getAttributes().getNumInstructions(c, ignoreIterations); 
			}			
			setCachedNumInstructions(c, value);
			setCachedNumInstructionsValid(c, true);
		}
		return getCachedNumInstructions(c);
	}

	public long getNumFNops(Case c, boolean ignoreIterations) {
		if (!getCachedNumFnopsValid(c) && !ignoreIterations) {
			long value = 0;
			for (Node child : getNode().getChildren()) {
				if (m_setOptions.get(child))
				    value += child.getAttributes().getNumFNops(c, ignoreIterations);
			}		
			Node option = getNode().getUnsetOption(c, ignoreIterations);
			if (option != null) {
				value += option.getAttributes().getNumFNops(c, ignoreIterations); 
			}			
			setCachedNumFnops(c, value);
			setCachedNumFnopsValid(c, true);
		}
		return getCachedNumFnops(c);
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
	public boolean setLoopPathIterations(Node loopNode, Node optionNode, long numIterations) {
		long loopIterations = loopNode.getAttributes().getLoopIterations();
		SectionNode sectionNode = (SectionNode)getNode();
		
		// Calculates the total iterations already set on all options of 
		// this section (excluding the contribution from the option we are
		// try to set here)
		long totalNumIterationsSet = getTotalNumIterationsSet();
		
		// Check that we will not set an iteration count that
		// overflows the iteration count set on the enclosing loop...
   		long newTotal = totalNumIterationsSet + numIterations;
   		if (newTotal > loopIterations)
   			return false;

   		// If there is a remainder, checks that we have at least 
   		// one option that hasn't had its iterations set...
   		long remainder = loopIterations - newTotal;
   		if (remainder > 0) {
   			int numUnsetOptions = 0;
   	   		for (Node node : m_setOptions.keySet()) {
   	   			if (!m_setOptions.get(node) && (node != optionNode))
   	   				++numUnsetOptions;
   	   		}
   			if (numUnsetOptions == 0) {
   				if (!sectionNode.isSectionOptional()) {
   	   	   			return false;   					
   				}
   			}
   		}

   		// Sets the iterations on the given section option node..
   		m_numIterations.put(optionNode, numIterations);
   		m_setOptions.put(optionNode, true);
		return true;
	}
	
	public long getLoopPathIterations(Node loopNode, Node optionNode, Case c) {
		assert(m_numIterations.containsKey(optionNode));
		
		if (getSetOption(optionNode)) {
			// The iterations have been explicitly set for this option.
			return m_numIterations.get(optionNode);	
			
		} else {
			// Need to assign any remaining iterations to the best/worst case.
			long loopIterations = loopNode.getAttributes().getLoopIterations();
			long totalNumIterationsSet = getTotalNumIterationsSet();
	   		long remainder = loopIterations - totalNumIterationsSet;
	   		if (remainder > 0) {
				Node option = getNode().getUnsetOption(c, true);
				if (option != null) {
					if (option == optionNode)
						return remainder;
				}				
	   		}			
			return 0;			
		}
	}
	
	public boolean getSetOption(Node optionNode) {
		assert(m_setOptions.containsKey(optionNode));
		return m_setOptions.get(optionNode);
	}
	
	public void resetPathIterations() {
		clearNumIterations();
		clearSetOptions();
	}
	
	private void clearNumIterations() {
   		for (Node node : m_numIterations.keySet()) {
   			m_numIterations.put(node, (long)0);
   		}		
	}

	private void clearSetOptions() {
   		for (Node node : m_setOptions.keySet()) {
   			m_setOptions.put(node, false);
   		}		
	}
	
	private void initialiseNumIterations() {
		m_numIterations = new HashMap<Node, Long>();
   		for (Node node : getNode().getChildren()) {
   			m_numIterations.put(node, (long)0);
   		}		
	}
	
	private void initialiseSetOptions() {
		m_setOptions = new HashMap<Node, Boolean>();
   		for (Node node : getNode().getChildren()) {
   			m_setOptions.put(node, false);
   		}		
	}
	
	private long getTotalNumIterationsSet() {
		long totalNumIterationsSet = 0;
		for (Node option : m_setOptions.keySet()) {
			if (m_setOptions.get(option)) {
				totalNumIterationsSet += m_numIterations.get(option);
			}
	    }
  	    return totalNumIterationsSet;
	}
}
