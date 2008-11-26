package com.xmos.xta.rcp.views.Structure;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import com.xmos.xta.rcp.nodes.Node;

public class StructureLoopNode extends StructureNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 3;
    private int m_bottomBorder = 3;
    private int m_connectionY = 10;
    private int m_cornerRounding = 7;
    
    private int m_x;
    private int m_y;
    private int m_width;
    private int m_height;
    
	public StructureLoopNode(Node treeNode, double weight) {
		super(treeNode, weight);
		
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

	public void draw(Display display, GC gc, ArrayList<StructureNode> clickAreas,
			int xOffset, int yOffset, double scale) {
        if (DEBUG) System.out.printf("Draw Loop %d,%d,%f\n", xOffset, yOffset, scale);

        m_x = (int)((double)(xOffset + m_borderX) * scale);
        m_y = (int)((double)yOffset * scale);
        m_width = (int)((double)(getPixelsX() - (2 * m_borderX)) * scale);
        m_height = (int)((double)(getPixelsY() - m_bottomBorder) * scale);
        
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
    	gc.drawRoundRectangle(m_x, m_y, m_width, m_height, m_cornerRounding, m_cornerRounding);

        int mid = (int)(((double)xOffset + ((double)getPixelsX() / 2)) * scale);
    	int childOffsetX = m_pixelsX / 2;
    	int childTop = (int)((double)(yOffset + m_connectionY) * scale);
    	for (StructureNode child : m_children) {
    		child.draw(display, gc, clickAreas,
    				xOffset + childOffsetX, yOffset + m_connectionY, scale);
    		int childX = child.getPixelsX();

    		int childMid = (int)((double)(xOffset + childOffsetX + (childX / 2)) * scale);
        	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawLine(mid, m_y, childMid, childTop);

    		childOffsetX += childX;
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
