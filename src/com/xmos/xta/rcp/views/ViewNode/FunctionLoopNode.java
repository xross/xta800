package com.xmos.xta.rcp.views.ViewNode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import com.xmos.xta.rcp.nodes.Node;

public class FunctionLoopNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_numIterations;

    private int m_borderX = 3;

    private int m_left;
    private int m_right;
    private int m_bottom;
    private int m_top;
    
	public FunctionLoopNode(Node treeNode, int numIterations, int lineWidth) {
		super(treeNode, lineWidth);
		
		m_numIterations = numIterations;
		if (m_numIterations > 0) {
			m_pixelsX = m_borderX * 2;
			m_pixelsY = 20;
		} else {
			m_pixelsX = 0;
			m_pixelsY = 0;
		}
	}
	
	public int getPixelsX() {
		if (m_numIterations == 0)
			return 0;

		int pixels = getChildrenX() + m_pixelsX;
		if (DEBUG) System.out.printf("FunctionLoop X %d\n", pixels);
		return pixels;
	}

	public int getPixelsY() {
		if (m_numIterations == 0)
			return 0;

		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("FunctionLoop Y %d\n", pixels);
		return pixels;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getNodes()) {
			ViewNode child = treeNode.getFunctionNode();
			if (child != null) {
				pixels += child.getPixelsX();
			}
		}
		return pixels;
	}

	public int getChildrenY() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getNodes()) {
			ViewNode child = treeNode.getFunctionNode();
			if (child != null) {
				int childPixels = child.getPixelsY();
				if (childPixels > pixels)
					pixels = childPixels;
			}
		}
		return pixels;
	}

	public void draw(Display display, GC gc, double scale) {
        if (m_numIterations == 0)
        	return;

        if (DEBUG) System.out.printf("Draw FunctionLoop %d,%d,%f\n",
        		m_pixelOffsetX, m_pixelOffsetY, scale);

        gc.setLineWidth(m_lineWidth);
        int childrenX = getChildrenX();
        m_left = scale(m_pixelOffsetX, scale);
        m_right = scale(m_pixelOffsetX + childrenX + (m_borderX * 2), scale);
        m_top = m_pixelOffsetY;
        m_bottom = m_pixelOffsetY + getPixelsY();
        
        gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
        if (m_isSelected) gc.setLineWidth(m_lineWidth + 1);
        gc.drawLine(m_left, m_top, m_left, m_bottom);
        gc.drawLine(m_right, m_top, m_right, m_bottom);
        gc.drawLine(m_left, m_bottom, scale(m_pixelOffsetX + m_borderX, scale), m_bottom);
        gc.drawLine(scale(m_pixelOffsetX + m_borderX + childrenX, scale), m_bottom, m_right, m_bottom);
        
		for (Node treeNode : m_treeNode.getNodes()) {
			ViewNode child = treeNode.getFunctionNode();
			if (child != null) {
	    		child.draw(display, gc, scale);
			}
    	}
	}

	public void annotateXy(int xOffset, int yOffset, ArrayList<ViewNode> clickAreas) {
        m_pixelOffsetX = xOffset;
        m_pixelOffsetY = yOffset;

        int childOffsetX = m_borderX;
		for (Node treeNode : m_treeNode.getNodes()) {
			ViewNode child = treeNode.getFunctionNode();
			if (child != null) {
	    		child.annotateXy(xOffset + childOffsetX, yOffset + m_pixelsY, clickAreas);
	    		childOffsetX += child.getPixelsX();
			}
    	}

    	clickAreas.add(this);
	}

	public boolean contains(int x, int y) {
		if ((x >= m_left) && (x <= m_right)) {
			if ((y >= m_top) && (y <= m_bottom)) {
				return true;
			}
		}
		return false;
	}
}
