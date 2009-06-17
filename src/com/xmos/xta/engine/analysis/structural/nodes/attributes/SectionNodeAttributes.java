package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public void setLoopIterations(Case c, long numIterations) {
    	setNumIterations(c, numIterations);
    	
    	// Allocates the best/worst case iterations to the best/worst 
       	// case child options, and assigns zero to the best/worst case of 
       	// all the other options.
       	SectionNode sectionNode = (SectionNode)getNode();	    	
       	Node caseNode = sectionNode.getOption(c, true);
   		for (Node child : sectionNode.getChildren()) {
   			if (child == caseNode) {
   				child.getAttributes().setLoopIterations(c, numIterations);
   			} else {
   			    child.getAttributes().setLoopIterations(c, 0);
   			}   			
   			m_setOptions.put(child, false);
    	}
	}	

	public boolean setLoopPathIterations(Node loopNode, Node optionNode, long numIterations) {
		long loopIterations = loopNode.getAttributes().getNumIterations(Case.WORST);
		SectionNode sectionNode = (SectionNode)getNode();
		
		// Calculates the total iterations already set on all options of 
		// this section (excluding the contribution from the option we are
		// try to set here)
		long previouslySetIterationTotal = 0;
   		for (Node child : getNode().getChildren()) {
   			if ((child != optionNode) && m_setOptions.get(child)) {
   				previouslySetIterationTotal += child.getAttributes().getNumIterations(Case.WORST);
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
   		if (remainder > 0) {
   			List<Node> unsetNodes = sectionNode.getAllUnsetOptions(); 
   			unsetNodes.remove(optionNode);
   			if (unsetNodes.size() == 0)
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
   		
   		// Allocates the remainder to the best/worst case of the options
   		// that have not been explicitly set by the user.
   		if (remainder > 0) {
   			sectionNode.getOption(Case.WORST, true).getAttributes().setLoopIterations(Case.WORST, remainder);
   			sectionNode.getOption(Case.BEST, true).getAttributes().setLoopIterations(Case.BEST, remainder);
   		}   	
		return true;
	}
	
	public Map<Node, Boolean> getSetOptions() {
		return m_setOptions;
	}	
}
