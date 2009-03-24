package com.xmos.xta.rcp.views.ViewNode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.rcp.nodes.BlockNode;
import com.xmos.xta.rcp.nodes.Node;

public class FunctionPathNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_borderY = 3;

    private int m_x;
    private int m_y;
    private int m_width;
    private int m_height;

	public FunctionPathNode(Node treeNode) {
		super(treeNode);
		m_pixelsX = 0;
	}

	public void setLineWidth(int lineWidth) {
		m_lineWidth = lineWidth + 1;
	}
	
	public void setNumIterations(long numIterations) {
		m_numIterations = numIterations;
		if (m_numIterations > 0) {
			m_pixelsY = 2 * m_borderY + m_lineWidth;
		} else {
			m_pixelsY = 0;
		}
	}
	
	public int getPixelsX() {
		if (m_numIterations == 0)
			return 0;

		int pixels = getChildrenX() + m_pixelsX;
		if (DEBUG) System.out.printf("FunctionPath X %d\n", pixels);
		return pixels;
	}

	public int getPixelsY() {
		if (m_numIterations == 0)
			return 0;

		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("FunctionPath Y %d\n", pixels);
		return pixels;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			ViewNode child = treeNode.getFunctionNode();
			if (child != null) {
				pixels += child.getPixelsX();
			}
		}
		return pixels;
	}

	public int getChildrenY() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			ViewNode child = treeNode.getFunctionNode();
			if (child != null) {
				int childPixels = child.getPixelsY();
				if (childPixels > pixels)
					pixels = childPixels;
			}
		}
		return pixels;
	}
	
	private int getBlockY() {
		for (Node treeNode : m_treeNode.getChildren()) {
			if (treeNode instanceof BlockNode) {
				ViewNode child = treeNode.getFunctionNode();
				if (child != null) {
					return child.getPixelsY();
				}
			}
		}
		return 0;
	}

	public void draw(Display display, GC gc, double scale) {
        if (DEBUG) System.out.printf("Draw FunctionPath %d,%d,%f\n",
        		m_pixelOffsetX, m_pixelOffsetY, scale);

        if (m_numIterations == 0)
        	return;
        
        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX, scale);
        m_y = m_pixelOffsetY;
        m_width = scale(getPixelsX(), scale);
        m_height = getBlockY() + m_pixelsY;

        gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.drawLine(m_x, m_y, m_x, m_y);

        int childOffsetX = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			ViewNode child = treeNode.getFunctionNode();
			if (child != null) {
	    		child.draw(display, gc, scale);
	            gc.setLineWidth(m_lineWidth);
	    		int childX = child.getPixelsX(); 

	    		if (child instanceof FunctionLoopNode) gc.setLineStyle(SWT.LINE_DASH);
	    		gc.drawLine(m_x + scale(childOffsetX, scale), m_y,
	    				m_x + scale(childX + childOffsetX, scale), m_y);
	    		if (child instanceof FunctionLoopNode) gc.setLineStyle(SWT.LINE_SOLID);
				
	            childOffsetX += childX;
			}
    	}
    	gc.drawLine(m_x + scale(childOffsetX, scale), m_y,
    			m_x + scale(childOffsetX, scale), m_y);
	}

	public void annotateXy(int xOffset, int yOffset) {
        m_pixelOffsetX = xOffset;
        m_pixelOffsetY = yOffset;

        int childOffsetX = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			ViewNode child = treeNode.getFunctionNode();
    		child.annotateXy(xOffset + childOffsetX, yOffset + m_borderY + m_lineWidth);
            childOffsetX += child.getPixelsX();
    	}
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
		for (Node treeNode : m_treeNode.getChildren()) {
			ViewNode child = treeNode.getFunctionNode();
			child.buildClickAreas(clickAreas);
    	}
		clickAreas.add(this);
	}

	public boolean contains(int x, int y) {
		if ((x >= m_x) && (x <= (m_x + m_width))) {
			if ((y >= m_y) && (y <= (m_y + m_height))) {
				return true;
			}
		}
		return false;
	}
}
