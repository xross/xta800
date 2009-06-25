package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.TerminalNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class TerminalNodeAttributes extends NodeAttributes {
	private Map<Node, Boolean> m_setOptions;

	public TerminalNodeAttributes(Node node) {
		super(node);
		m_setOptions = new HashMap<Node, Boolean>();
   		for (Node child : getNode().getChildren()) {
   			m_setOptions.put(child, false);
   		}		
	}

	public long getNumPaths() {
		long value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getNumPaths();
		}
		return value;
	}

	public void setLoopIterations(Case c, long numIterations) {
    	setNumIterations(c, numIterations);

    	// Allocates the best/worst case iterations to the best/worst 
       	// case child options, and assigns zero to the best/worst case of 
       	// all the other options.
       	TerminalNode terminalNode = (TerminalNode)getNode();	    	
       	Node caseNode = terminalNode.getOption(c, true);
   		for (Node child : terminalNode.getChildren()) {
   			if (child == caseNode) {
   				child.getAttributes().setLoopIterations(c, numIterations);
   			} else {
   			    child.getAttributes().setLoopIterations(c, 0);
   			}   			
   			m_setOptions.put(child, false);
    	}
	}
	
	public Map<Node, Boolean> getSetOptions() {
		return m_setOptions;
	}		
}
