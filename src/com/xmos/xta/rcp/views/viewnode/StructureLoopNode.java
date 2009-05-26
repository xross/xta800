package com.xmos.xta.rcp.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import com.xmos.xta.rcp.nodes.Node;

public class StructureLoopNode extends StructureNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 4;
    private int m_bottomBorder = 4;
    private int m_connectionY = 10;
    private int m_cornerRounding = 7;
    
    private int m_x;
    private int m_y;
    private int m_width;
    private int m_height;
    
	public StructureLoopNode(Node treeNode) {
		super(treeNode);
		
		m_pixelsX = 2 * m_borderX;
		m_pixelsY = m_connectionY + m_bottomBorder;
	}
	
	public int getPixelsX() {
		int pixels = getChildrenX() + m_pixelsX;
		if (DEBUG) System.out.printf("StructureLoop X %d\n", pixels);
		return pixels;
	}

	public int getPixelsY() {
		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("StructureLoop Y %d\n", pixels);
		return pixels;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			StructureNode child = treeNode.getStructureNode();
			if (child != null) {
				pixels += child.getPixelsX();
			}
		}
		return pixels;
	}

	public int getChildrenY() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			StructureNode child = treeNode.getStructureNode();
			if (child != null) {
				int childPixels = child.getPixelsY();
				if (childPixels > pixels)
					pixels = childPixels;
			}
		}
		return pixels;
	}

	public void draw(Display display, GC gc, double scale) {
        if (DEBUG) System.out.printf("Draw StructureLoop %d,%d,%f\n",
        		m_pixelOffsetX, m_pixelOffsetY, scale);

        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX + m_borderX, scale);
        m_y = scale(m_pixelOffsetY, scale);
        m_width = scale(getPixelsX() - (2 * m_borderX), scale);
        m_height = scale(getPixelsY() - m_bottomBorder, scale);
        
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));

        if (m_isSelected) gc.setLineWidth(m_lineWidth + 1);
    	gc.drawRoundRectangle(m_x, m_y, m_width, m_height, m_cornerRounding, m_cornerRounding);
        if (m_isSelected) gc.setLineWidth(m_lineWidth);

        int mid = scale(m_pixelOffsetX + (getPixelsX() / 2), scale);
    	int childOffsetX = m_pixelsX / 2;
    	int childTop = scale(m_pixelOffsetY + m_connectionY, scale);
		for (Node treeNode : m_treeNode.getChildren()) {
			StructureNode child = treeNode.getStructureNode();
			if (child != null) {
	    		child.draw(display, gc, scale);
	    		int childX = child.getPixelsX();
	
	    		int childMid = scale(m_pixelOffsetX + childOffsetX + (childX / 2), scale);
	        	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	            gc.drawLine(mid, m_y, childMid, childTop);
	
	    		childOffsetX += childX;
			}
    	}
	}

	public void annotateXy(int xOffset, int yOffset) {
        m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;

        int childOffsetX = m_borderX;
		for (Node treeNode : m_treeNode.getChildren()) {
			StructureNode child = treeNode.getStructureNode();
			if (child != null) {
	    		child.annotateXy(xOffset + childOffsetX, yOffset + m_connectionY);
	    		childOffsetX +=  child.getPixelsX();
			}
    	}
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
		for (Node treeNode : m_treeNode.getChildren()) {
			StructureNode child = treeNode.getStructureNode();
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
