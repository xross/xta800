package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class InfiniteSelfLoopNodeAttributes extends NodeAttributes {
	public InfiniteSelfLoopNodeAttributes(Node node) {
		super(node);
	}
	
	long getLocalIterations(Node child) 
        throws SectionIterationsNotSetException, InfiniteIterationsException {
	    throw new InfiniteIterationsException();
    }
	
	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveSection)
        throws IgnoreSectionIterationsException, InfiniteIterationsException {
	    throw new InfiniteIterationsException();		
    }	
}
