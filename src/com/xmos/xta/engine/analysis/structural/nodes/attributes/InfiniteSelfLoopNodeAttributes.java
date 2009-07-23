package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class InfiniteSelfLoopNodeAttributes extends NodeAttributes {
	public InfiniteSelfLoopNodeAttributes(Node node) {
		super(node);
	}
	
	Iterations getTotalIterations(Case c, Node child, Node ignoreIterationsAboveSection)
        throws IgnoreSectionIterationsException, InfiniteIterationsException {
	    throw new InfiniteIterationsException();		
    }	
}
