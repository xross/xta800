package com.xmos.xta.rcp.views.Structure;

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
    
	public StructureRouteNode(Node treeNode, double weight) {
		super(treeNode, weight);
		
		m_pixelsX = 0;
		m_pixelsY = m_topBorder + m_bottomBorder;
	}

	public int getPixelsX() {
		int pixels = getChildrenX();
		if (pixels < m_pixelsX)
			pixels = m_pixelsX; 
			
		if (DEBUG) System.out.printf("Route X %d\n", m_pixelsX);
		return pixels;
	}

	public int getPixelsY() {
		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("Route Y %d\n", pixels);
		return pixels;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (StructureNode child : m_children) {
			pixels += child.getPixelsX();
		}
		pixels += (m_children.size() - 1) * m_pathDividerGap;
		return pixels;
	}

	public int getChildrenY() {
		int pixels = 0;
		for (StructureNode child : m_children) {
			int childPixels = child.getPixelsY();
			if (childPixels > pixels)
				pixels = childPixels;
		}
		return pixels;
	}

	public void draw(Display display, GC gc, ArrayList<StructureNode> clickAreas,
			int xOffset, int yOffset, double scale) {
        if (DEBUG) System.out.printf("Draw Route %d,%d,%f\n", xOffset, yOffset, scale);

    	int childOffsetX = 0;
    	int childNum = 0;
    	for (StructureNode child : m_children) {

    		if (childNum > 0) {
        		drawDivider(display, gc, xOffset + childOffsetX + 
        				(m_pathDividerGap - m_pathDividerLineWidth) / 2,
        				yOffset, scale);
    			childOffsetX += m_pathDividerGap;
    		}

    		child.draw(display, gc, clickAreas,
    				xOffset + childOffsetX, yOffset + m_topBorder, scale);
    		childOffsetX += child.getPixelsX();
    		
    		childNum++;
    	}

    	clickAreas.add(this);
    }

	private void drawDivider(Display display, GC gc, int xOffset, int yOffset, double scale) {
        if (DEBUG) System.out.printf("Draw Divider %d,%d,%f\n", xOffset, yOffset, scale);

        int x = (int)((double)xOffset * scale);
        int top = (int)(double)(yOffset * scale);
        int bottom = (int)((double)(yOffset + getPixelsY()) * scale);

        gc.setAlpha(100);
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.drawLine(x, top, x, bottom);
        gc.setAlpha(255);
    }

	public boolean contains(int x, int y) {
		return false;
	}
}
