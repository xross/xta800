package com.xmos.xta.rcp.views.Structure;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.rcp.nodes.Node;

public abstract class StructureNode {
    private Node m_treeNode;
    protected Vector<StructureNode> m_children;

    protected double m_weight;
    protected int m_pixelsX;
    protected int m_pixelsY;
    protected int m_childrenX;
    protected int m_childrenY;
	
    public StructureNode(Node treeNode, double weight) {
    	m_treeNode = treeNode;
    	m_weight = weight;

    	m_children = new Vector<StructureNode>();
		m_childrenX = 0;
		m_childrenY = 0;
    }    
    
    public int numChildren() {
    	return m_children.size();
	}

	public void addChild(StructureNode node) {
    	m_children.add(node);
    }

    public Vector<StructureNode> getChildren() {
    	return m_children;
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

    public abstract int getPixelsX();
    public abstract int getPixelsY();
    public abstract int getChildrenX();
    public abstract int getChildrenY();
    public abstract void draw(Display display, GC gc, ArrayList<StructureNode> clickAreas,
    		int xOffset, int yOffset, double scale);
    public abstract boolean contains(int x, int y);
}
