package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class InfiniteLoopNodeAttributes extends NodeAttributes {
	public InfiniteLoopNodeAttributes(Node node) {
		super(node);
	}
	
	long getTotalIterations(Case c, Node child, Node ignoreIterationsAboveSection)
        throws IgnoreSectionIterationsException, InfiniteIterationsException {
	    throw new InfiniteIterationsException();		
    }	
}
