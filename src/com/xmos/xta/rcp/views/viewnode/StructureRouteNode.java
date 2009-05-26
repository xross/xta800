package com.xmos.xta.rcp.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import com.xmos.xta.rcp.nodes.Node;

public class StructureRouteNode extends StructureNode {
    public static final boolean DEBUG = false;

    private int m_pathDividerGap = 5;
    private int m_pathDividerLineWidth = 1;
    private int m_topBorder = 10;
    private int m_bottomBorder = 10;
    
	public StructureRouteNode(Node treeNode) {
		super(treeNode);
		
		m_pixelsX = 0;
		m_pixelsY = m_topBorder + m_bottomBorder;
	}

	public int getPixelsX() {
		int pixels = getChildrenX();
		if (pixels < m_pixelsX)
			pixels = m_pixelsX; 
			
		if (DEBUG) System.out.printf("StructureRoute X %d\n", m_pixelsX);
		return pixels;
	}

	public int getPixelsY() {
		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("StructureRoute Y %d\n", pixels);
		return pixels;
	}

	public int getChildrenX() {
		int pixels = 0;
		int numChildren = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			StructureNode child = treeNode.getStructureNode();
			if (child != null) {
				pixels += child.getPixelsX();
				numChildren++;
			}
		}
		if (numChildren > 0) {
			pixels += (numChildren - 1) * m_pathDividerGap;
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
        if (DEBUG) System.out.printf("Draw StructureRoute %d,%d,%f\n",
        		m_pixelOffsetX, m_pixelOffsetY, scale);

    	int childOffsetX = 0;
    	int childNum = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			StructureNode child = treeNode.getStructureNode();
			if (child != null) {
	    		if (childNum > 0) {
	        		drawDivider(display, gc, m_pixelOffsetX + childOffsetX + 
	        				(m_pathDividerGap - m_pathDividerLineWidth) / 2,
	        				m_pixelOffsetY, scale);
	    			childOffsetX += m_pathDividerGap;
	    		}
	
	    		child.draw(display, gc, scale);
	    		childOffsetX += child.getPixelsX();
	    		
	    		childNum++;
			}
    	}
    }

	private void drawDivider(Display display, GC gc, int xOffset, int yOffset, double scale) {
        if (DEBUG) System.out.printf("Draw Divider %d,%d,%f\n", xOffset, yOffset, scale);

        gc.setLineWidth(m_lineWidth);
        int x = scale(xOffset, scale);
        int top = scale(yOffset, scale);
        int bottom = scale(yOffset + getPixelsY(), scale);

        gc.setAlpha(100);
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.drawLine(x, top, x, bottom);
        gc.setAlpha(255);
    }

	public void annotateXy(int xOffset, int yOffset) {
        m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;

    	int childOffsetX = 0;
    	int childNum = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			StructureNode child = treeNode.getStructureNode();
			if (child != null) {
	    		if (childNum > 0)
	    			childOffsetX += m_pathDividerGap;
	
	    		child.annotateXy(xOffset + childOffsetX, yOffset + m_topBorder);
	    		childOffsetX += child.getPixelsX();
	    		childNum++;
			}
    	}
    }

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
		for (Node treeNode : m_treeNode.getChildren()) {
			StructureNode child = treeNode.getStructureNode();
			child.buildClickAreas(clickAreas);
    	}
    }

	public boolean contains(int x, int y) {
		return false;
	}
}
