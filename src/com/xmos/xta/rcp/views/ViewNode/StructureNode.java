package com.xmos.xta.rcp.views.ViewNode;

import com.xmos.xta.rcp.nodes.Node;

public abstract class StructureNode extends ViewNode {
    protected double m_weight;
	
    public StructureNode(Node treeNode, double weight, int lineWidth) {
    	super(treeNode, lineWidth);
    	m_weight = weight;
    }    
}
