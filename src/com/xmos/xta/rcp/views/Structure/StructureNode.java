package com.xmos.xta.rcp.views.Structure;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.rcp.nodes.Node;

public abstract class StructureNode {
    protected Node m_treeNode;
    protected double m_weight;
    protected int m_lineWidth;
    protected int m_pixelsX;
    protected int m_pixelsY;
    protected int m_childrenX;
    protected int m_childrenY;
    protected boolean m_isSelected;
	
    public StructureNode(Node treeNode, double weight, int lineWidth) {
    	m_treeNode = treeNode;
    	m_weight = weight;
    	m_lineWidth = lineWidth;

		m_childrenX = 0;
		m_childrenY = 0;
		
		m_isSelected = false;
    }    
    
    public Node getTreeNode() {
    	return m_treeNode;
    }

    public double getWidth(double scale) {
    	return m_pixelsX * scale;
    }

    public double getHeight(double scale) {
    	return m_pixelsY * scale;
    }

	public boolean isSelected() {
		return m_isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		m_isSelected = isSelected;
	}

    public abstract int getPixelsX();
    public abstract int getPixelsY();
    public abstract int getChildrenX();
    public abstract int getChildrenY();
    public abstract void draw(Display display, GC gc, ArrayList<StructureNode> clickAreas,
    		int xOffset, int yOffset, double scale);
    public abstract boolean contains(int x, int y);
}
