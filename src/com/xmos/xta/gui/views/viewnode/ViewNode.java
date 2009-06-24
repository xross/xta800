package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public abstract class ViewNode {
    protected Node m_treeNode;
    protected int m_lineWidth;
    protected int m_pixelOffsetX;
    protected int m_pixelOffsetY;
    protected int m_pixelsX;
    protected int m_pixelsY;
    protected int m_childrenX;
    protected int m_childrenY;

    protected long m_numIterations;
    protected boolean m_isSelected;
	
    private List<ViewNode> m_children;
    
    public ViewNode(Node treeNode) {
    	m_treeNode = treeNode;
    	m_lineWidth = 0;

		m_childrenX = 0;
		m_childrenY = 0;

	    m_pixelOffsetX = 0;
	    m_pixelOffsetY = 0;

	    m_pixelsX = 0;
	    m_pixelsY = 0;

		m_isSelected = false;
		
		m_children = new ArrayList<ViewNode>();
    }
    
    public void setLineWidth(int lineWidth) {
    	m_lineWidth = lineWidth;
    }
    
    public void setNumIterations(long numIterations) {
    	m_numIterations = numIterations;
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

    public int getPixelOffsetX() {
    	return m_pixelOffsetX;
    }

    public int getPixelOffsetY() {
    	return m_pixelOffsetY;
    }

    public int scale(int x, double scale) {
    	return (int)((double)x * scale);
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
    public abstract void draw(Display display, GC gc, double scale);
    public abstract void annotateXy(int xOffset, int yOffset);
    public abstract void buildClickAreas(ArrayList<ViewNode> clickAreas);
    public abstract boolean contains(int x, int y);

	public String getToolTipText() {
		return m_treeNode.getAttributes().getTimingDetails();
	}

	public void addChild(ViewNode child) {
		m_children.add(child);
	}
	
	public List<ViewNode> getChildren() {
		return m_children;
	}

	public void drawConnection(Display display, GC gc, int x1, int y1, int x2, int y2) {
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
        gc.setLineWidth(m_lineWidth + 1);
		gc.drawLine(x1, y1, x2, y2);
		gc.setLineWidth(m_lineWidth);

	}
}
