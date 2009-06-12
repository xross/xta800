package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;

public class SectionNodeAttributes extends NodeAttributes {
	public SectionNodeAttributes(Node node) {
		super(node);
	}

	public void setLoopIterations(long numIterations) {
    	setNumIterations(numIterations);

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
}
