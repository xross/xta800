package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class SectionNodeAttributes extends NodeAttributes {
	private Map<Node, Boolean> m_setOptions;
	
	public SectionNodeAttributes(Node node) {
		super(node);
		m_setOptions = new HashMap<Node, Boolean>();
   		for (Node child : getNode().getChildren()) {
   			m_setOptions.put(child, false);
   		}		
	}

	public void setLoopIterations(long numIterations) {
    	setNumIterations(numIterations);

    	// Allocates the iterations to the worst case child option, 
    	// and assigns zero to all the other options.
       	SectionNode sectionNode = (SectionNode)getNode();	    	
       	Node worstCaseNode = sectionNode.getWorstCaseOption();
   		for (Node child : sectionNode.getChildren()) {
   			if (child == worstCaseNode) {
   				child.getAttributes().setLoopIterations(numIterations);
   			} else {
   			    child.getAttributes().setLoopIterations(0);
   			}
    	}
	}	

	public boolean setLoopPathIterations(Node loopNode, Node optionNode, long numIterations) {
		long loopIterations = loopNode.getAttributes().getNumIterations();
		
		// Calculates the total iterations already set on all options of 
		// this section (excluding the contribution from the option we are
		// try to set here)
		long previouslySetIterationTotal = 0;
   		for (Node child : getNode().getChildren()) {
   			if ((child != optionNode) && m_setOptions.get(child)) {
   				previouslySetIterationTotal += child.getAttributes().getNumIterations();
   			} 
   		}

		// Check that we will not set an iteration count that
		// overflows the iteration count set on the enclosing loop...
   		long newTotal = previouslySetIterationTotal + numIterations;
   		if (newTotal > loopIterations)
   			return false;

   		// if there is a remainder, checks that we have at least one option
   		// that hasn't had its iterations set...
   		long remainder = loopIterations - newTotal;
   		Node worstCaseUnsetOption = null;
   		if (remainder > 0) {
   			worstCaseUnsetOption = getWorstCaseUnsetOption(optionNode);
   			if (worstCaseUnsetOption == null)
   	   			return false;
   		}

   		// Sets the iterations on the given section option node..
   		optionNode.getAttributes().setLoopIterations(numIterations);
   		m_setOptions.put(optionNode, true);
   		
   		// Clears all the options that have not been explicitly set.
   		for (Node child : getNode().getChildren()) {
   			if (!m_setOptions.get(child))
   				child.getAttributes().setLoopIterations(0);
   		}
   		
   		// Allocates the remainder to the worst case of the options
   		// that have not been explicitly set by the user.
   		if (remainder > 0) {
   		    worstCaseUnsetOption.getAttributes().setLoopIterations(remainder);
   		}   		
		return true;
	}
	
	private Node getWorstCaseUnsetOption(Node optionNode) {
		// Builds up a set of all the unset options..
		Set<Node> unsetOptions = new HashSet<Node>(); 		
   		for (Node child : getNode().getChildren()) {
   			if ((child != optionNode) && !m_setOptions.get(child))
   				unsetOptions.add(child);
   		}
   		if (unsetOptions.size() == 0)
   			return null;
				
   		// Finds the worst case of the above list..
		Iterator<Node> iter = unsetOptions.iterator();
		Node worstCaseNode = iter.next();
		while (iter.hasNext()) {
			Node node = iter.next();
			if (node.getTimeTaken(Case.WORST) > worstCaseNode.getTimeTaken(Case.WORST)) {
				worstCaseNode = node;
			}						
		}		
		return worstCaseNode;
	}
}
