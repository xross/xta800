package com.xmos.xta.rcp.views.ViewNode;

import com.xmos.xta.rcp.nodes.Node;

public abstract class StructureNode extends ViewNode {
    protected double m_weight;
    protected boolean m_isSelected;
	
    public StructureNode(Node treeNode, double weight, int lineWidth) {
    	super(treeNode, lineWidth);
    	m_weight = weight;
		m_isSelected = false;
    }    
    
	public boolean isSelected() {
		return m_isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		m_isSelected = isSelected;
	}
}
