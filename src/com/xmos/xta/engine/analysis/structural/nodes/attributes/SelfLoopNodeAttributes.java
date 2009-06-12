package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class SelfLoopNodeAttributes extends NodeAttributes {
	public SelfLoopNodeAttributes(Node node) {
		super(node);
	}
	/*
	public void setNumIterations(long numIterations) {
	    if (!m_numIterationsDone) {
	    	m_numIterationsDone = true;
	    	
			for (Node child : getNode().getChildren()) {
				child.getAttributes().setNumIterations(numIterations);
			}
	    }
	}	
	*/	
}
