package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import com.xmos.xta.gui.nodes.Node;

public class StructurePathNode extends StructureNode {
    public static final boolean DEBUG = false;

    private int m_blockX = 10;
    private int m_blockY = 20;
    private int m_connectorY = 7;
    private int m_borderX = 5;
    private int m_borderY = 5;

    private int m_x;
    private int m_y;
    private int m_width;
    private int m_height;

	public StructurePathNode(Node treeNode) {
		super(treeNode);
		
		m_pixelsX = m_blockX + (2 * m_borderX);
		m_pixelsY = m_blockY + m_connectorY + m_borderY;
	}

	public int getPixelsX() {
		int pixels = getChildrenX() + 2 * m_borderX;
		if (pixels < m_pixelsX)
			pixels = m_pixelsX;

		if (DEBUG) System.out.printf("StructurePath X %d\n", pixels);
		return pixels;
	}

	public int getPixelsY() {
		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("StructurePath Y %d\n", pixels);
		return pixels;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			if (!treeNode.isBlockNode()) {
				StructureNode child = treeNode.getStructureNode();
				pixels += child.getPixelsX();
			}
		}
		return pixels;
	}

	public int getChildrenY() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			if (!treeNode.isBlockNode()) {
				StructureNode child = treeNode.getStructureNode();
				int childPixels = child.getPixelsY();
				if (childPixels > pixels)
					pixels = childPixels;
			}
		}
		return pixels;
	}

	public void draw(Display display, GC gc, double scale) {
        if (DEBUG) System.out.printf("Draw StructurePath %d,%d,%f\n",
        		m_pixelOffsetX, m_pixelOffsetY, scale);

        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX + m_borderX, scale);
        m_y = scale(m_pixelOffsetY, scale);
        m_width = scale(getPixelsX() - (2 * m_borderX), scale);
        m_height = scale(m_pixelsY - m_borderY - m_connectorY, scale);

    	gc.setAlpha((int)(255.0 * m_weight));
        gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
    	gc.fillRectangle(m_x, m_y, m_width, m_height);

    	gc.setAlpha(255);
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        
        if (m_isSelected) gc.setLineWidth(m_lineWidth + 1);
    	gc.drawRectangle(m_x, m_y, m_width, m_height);
        if (m_isSelected) gc.setLineWidth(m_lineWidth);

    	int childOffsetX = m_borderX;
    	int childTop = scale(m_pixelOffsetY + m_pixelsY, scale);
    	int lineTop = scale(m_pixelOffsetY + m_pixelsY - m_borderY - m_connectorY, scale);
		for (Node treeNode : m_treeNode.getChildren()) {
			if (!treeNode.isBlockNode()) {
				StructureNode child = treeNode.getStructureNode();
	    		child.draw(display, gc, scale);
	    		int childX = child.getPixelsX();
	    		
	    		int lineX = scale(m_pixelOffsetX + childOffsetX + (childX / 2), scale);
	        	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	            gc.drawLine(lineX, lineTop, lineX, childTop);
	
	            childOffsetX += childX;
			}
    	}
	}

	public void annotateXy(int xOffset, int yOffset) {
        m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;

    	int childOffsetX = m_borderX;
		for (Node treeNode : m_treeNode.getChildren()) {
			if (!treeNode.isBlockNode()) {
				StructureNode child = treeNode.getStructureNode();
	    		child.annotateXy(xOffset + childOffsetX, yOffset + m_pixelsY);
	            childOffsetX += child.getPixelsX();
			}
    	}
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
		for (Node treeNode : m_treeNode.getChildren()) {
			if (!treeNode.isBlockNode()) {
				StructureNode child = treeNode.getStructureNode();
				child.buildClickAreas(clickAreas);
			}
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
