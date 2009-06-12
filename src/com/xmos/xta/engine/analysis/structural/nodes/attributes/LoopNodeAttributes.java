package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class LoopNodeAttributes extends NodeAttributes {
	public LoopNodeAttributes(Node node) {
		super(node);
	}
/*		
	public void setNumIterations(long numIterations, boolean recurse) {
	    if (!m_numIterationsDone) {
	    	m_numIterationsDone = true;
	    	
	    	LoopNode loopNode = (LoopNode)getNode();
	    	loopNode.getLoopHeader().getAttributes().setNumIterations(numIterations + 1);
			for (Node child : loopNode.getLoopBodies()) {
				child.getAttributes().setNumIterations(numIterations);
			}
	    }
	}
		*/
}
