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
    protected int m_childrenX;
    protected int m_childrenY;

    protected int m_cornerRounding = 7;
    protected int m_x;
    protected int m_y;
    protected int m_width;
    protected int m_height;

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
    public abstract void draw(Display display, GC gc, double scale, boolean isWorstCase);
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
        gc.setLineWidth(m_lineWidth + 1);
		gc.drawLine(x1, y1, x2, y2);
		gc.setLineWidth(m_lineWidth);
	}

	public void drawThis(Display display, GC gc) {
	    if (m_isSelected) {
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
	    	gc.setAlpha(100);
	    	gc.fillRoundRectangle(m_x, m_y, m_width, m_height, m_cornerRounding, m_cornerRounding);
	    	gc.setAlpha(255);
	    }
	}

	public void setConnectionColour(Display display, GC gc, boolean childIsWorstCase) {
		if (childIsWorstCase) {
			gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
		} else {
			gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
		}
	}

}
