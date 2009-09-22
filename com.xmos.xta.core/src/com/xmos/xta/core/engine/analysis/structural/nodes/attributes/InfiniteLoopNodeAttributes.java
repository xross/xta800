package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;

public class InfiniteLoopNodeAttributes extends NodeAttributes {
	public InfiniteLoopNodeAttributes(Node node) {
		super(node);
	}
	
	long getLocalIterations(Node child) 
	    throws CondIterationsNotSetException, InfiniteIterationsException {
		throw new InfiniteIterationsException();
	}
	
	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveCond)
        throws IgnoreCondIterationsException, InfiniteIterationsException {
	    throw new InfiniteIterationsException();		
    }	
}
