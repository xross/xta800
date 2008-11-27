package com.xmos.xta.rcp.views.Structure;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import com.xmos.xta.rcp.nodes.Node;

public class StructurePathNode extends StructureNode {
    public static final boolean DEBUG = false;

    private int m_blockX = 10;
    private int m_blockY = 20;
    private int m_connectorY = 5;
    private int m_borderX = 3;
    private int m_borderY = 3;

    private int m_x;
    private int m_y;
    private int m_width;
    private int m_height;

	public StructurePathNode(Node treeNode, double weight) {
		super(treeNode, weight);
		
		m_pixelsX = m_blockX + (2 * m_borderX);
		m_pixelsY = m_blockY + m_connectorY + m_borderY;
	}

	public int getPixelsX() {
		int pixels = getChildrenX() + 2 * m_borderX;
		if (pixels < m_pixelsX)
			pixels = m_pixelsX;

		if (DEBUG) System.out.printf("Path X %d\n", pixels);
		return pixels;
	}

	public int getPixelsY() {
		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("Path Y %d\n", pixels);
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
        if (DEBUG) System.out.printf("Draw Path %d,%d,%f\n", xOffset, yOffset, scale);

        m_x = (int)((double)(xOffset + m_borderX) * scale);
        m_y = (int)((double)yOffset * scale);
        m_width = (int)((double)(getPixelsX() - (2 * m_borderX)) * scale);
        m_height = (int)((double)(m_pixelsY - m_borderY - m_connectorY) * scale);

    	gc.setAlpha((int)(255.0 * m_weight));
        gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
        gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
    	gc.fillRectangle(m_x, m_y, m_width, m_height);

    	gc.setAlpha(255);
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.drawRectangle(m_x, m_y, m_width, m_height);

    	int childOffsetX = m_borderX;
    	int childTop = (int)((double)(yOffset + m_pixelsY) * scale);
    	int lineTop = (int)((double)(yOffset + m_pixelsY - m_borderY - m_connectorY) * scale);
    	for (StructureNode child : m_children) {
    		child.draw(display, gc, clickAreas,
    				xOffset + childOffsetX, yOffset + m_pixelsY, scale);
    		int childX = child.getPixelsX();
    		
    		int lineX = (int)((double)(xOffset + childOffsetX + (childX / 2)) * scale);
        	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawLine(lineX, lineTop, lineX, childTop);

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
