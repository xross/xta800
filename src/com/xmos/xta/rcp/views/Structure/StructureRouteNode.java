package com.xmos.xta.rcp.views.Structure;

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
    private int m_fontPadding = 5;

    private String m_header = "Route";
    private int m_headerY;
    
	public StructureRouteNode(int fontX, int fontY, Node treeNode, double weight) {
		super(treeNode, weight);
		
		// Enough room for borders
		m_pixelsX = m_header.length() * fontX;
		
		m_headerY = fontY + m_fontPadding;
		m_pixelsY = m_topBorder + m_bottomBorder + m_headerY;
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

	public void draw(Display display, GC gc, int xOffset, int yOffset, double scale) {
        if (DEBUG) System.out.printf("Draw Route %d,%d,%f\n", xOffset, yOffset, scale);

        int x = (int)((double)xOffset + ((double)(getPixelsX() - m_pixelsX) / 2.0) * scale);
        int y = (int)((double)yOffset * scale);
        int width = (int)((double)m_pixelsX * scale);
        int height = (int)((double)m_pixelsY * scale);

        if (DEBUG) System.out.printf("Route %d,%d - %d,%d\n", x, y, width, height);

        gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    	gc.drawText(m_header, x, y);
    	
    	int childOffsetX = 0;
    	int childNum = 0;
    	for (StructureNode child : m_children) {

    		if (childNum > 0) {
        		drawDivider(display, gc, xOffset + childOffsetX + 
        				(m_pathDividerGap - m_pathDividerLineWidth) / 2,
        				yOffset + m_headerY, scale);
    			childOffsetX += m_pathDividerGap;
    		}

    		child.draw(display, gc, xOffset + childOffsetX, yOffset + m_headerY + m_topBorder, scale);
    		childOffsetX += child.getPixelsX();
    		
    		childNum++;
    	}
    }

	private void drawDivider(Display display, GC gc, int xOffset, int yOffset, double scale) {
        if (DEBUG) System.out.printf("Draw Divider %d,%d,%f\n", xOffset, yOffset, scale);

        int x = (int)((double)xOffset * scale);
        int top = (int)(double)(yOffset * scale);
        int bottom = (int)((double)(yOffset + getPixelsY() - m_headerY) * scale);

        gc.setAlpha(100);
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.drawLine(x, top, x, bottom);
        gc.setAlpha(255);
    }
}
