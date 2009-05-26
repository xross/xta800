package com.xmos.xta.rcp.views.viewnode;

import com.xmos.xta.rcp.nodes.Node;

public abstract class StructureNode extends ViewNode {
    protected double m_weight;
	
    public StructureNode(Node treeNode) {
    	super(treeNode);
    	m_weight = 0;
    }    

    public double getWeight() {
    	return m_weight;
    }    

    public void setWeight(double weight) {
    	m_weight = weight;
    }    
}
