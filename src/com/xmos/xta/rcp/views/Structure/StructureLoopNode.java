package com.xmos.xta.rcp.views.Structure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import com.xmos.xta.rcp.nodes.Node;

public class StructureLoopNode extends StructureNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 3;
    private int m_bottomBorder = 3;
    private int m_connectionY = 10;

	public StructureLoopNode(Node treeNode, double weight) {
		super(treeNode, weight);
		
		// Enough room for borders
		m_pixelsX = 2 * m_borderX;
		m_pixelsY = m_connectionY + m_bottomBorder;
	}
	
	public int getPixelsX() {
		int pixels = getChildrenX() + m_pixelsX;
		if (DEBUG) System.out.printf("Loop X %d\n", pixels);
		return pixels;
	}

	public int getPixelsY() {
		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("Loop Y %d\n", pixels);
		return pixels;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (StructureNode child : m_children) {
			pixels += child.getPixelsX();
		}
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
        if (DEBUG) System.out.printf("Draw Loop %d,%d,%f\n", xOffset, yOffset, scale);

        int left = (int)((double)(xOffset + m_borderX) * scale);
        int right = (int)((double)(xOffset + getPixelsX() - m_borderX) * scale);
        int top = (int)((double)yOffset * scale);
        int bottom = (int)((double)(yOffset + getPixelsY() - m_bottomBorder) * scale);
        
        if (DEBUG) System.out.printf("Loop %d,%d - %d,%d\n", left, right, top, bottom);

    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
    	gc.drawLine(left, top, left, bottom);
    	gc.drawLine(left, top, right, top);
    	gc.drawLine(right, top, right, bottom);
    	gc.drawLine(left, bottom, right, bottom);

        int mid = (int)(((double)xOffset + ((double)getPixelsX() / 2)) * scale);
    	int childOffsetX = m_pixelsX / 2;
    	int childTop = (int)((double)(yOffset + m_connectionY) * scale);
    	for (StructureNode child : m_children) {
    		child.draw(display, gc, xOffset + childOffsetX, yOffset + m_connectionY, scale);
    		int childX = child.getPixelsX();

    		int childMid = (int)((double)(xOffset + childOffsetX + (childX / 2)) * scale);
        	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawLine(mid, top, childMid, childTop);

    		childOffsetX += childX;
    	}
	}
}
