package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class StructureFunctionNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_connectorLen = 7;
    private int m_borderX = 5;
    private int m_borderY = 5;

    private int m_x;
    private int m_y;
    private int m_width;
    private int m_height;

	public StructureFunctionNode(Node treeNode) {
		super(treeNode);
		
		m_pixelsX = 2 * m_borderX;
		m_pixelsY = 2 * m_borderY;
	}

	public int getPixelsX() {
		return getChildrenX() + m_pixelsX;
	}

	public int getPixelsY() {
		return getChildrenY() + m_pixelsY;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (ViewNode child : getChildren()) {
			pixels += child.getPixelsX();
		}
		pixels += (getChildren().size() - 1) * m_connectorLen;
		return pixels;
	}

	public int getChildrenY() {
		int pixels = 0;
		for (ViewNode child : getChildren()) {
			int childPixels = child.getPixelsY();
			if (childPixels > pixels)
				pixels = childPixels;
		}
		return pixels;
	}

	public void draw(Display display, GC gc, double scale) {
        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX, scale);
        m_y = scale(m_pixelOffsetY, scale);
        m_width = scale(getPixelsX(), scale);
        m_height = scale(getPixelsY(), scale);

        gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    	gc.fillRectangle(m_x, m_y, m_width, m_height);

        if (m_isSelected) {
        	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        	gc.setLineWidth(m_lineWidth + 1);
           	gc.drawRectangle(m_x, m_y, m_width, m_height);
        	gc.setLineWidth(m_lineWidth);
        }

    	int childOffsetX = m_borderX;
		for (ViewNode child : getChildren()) {
    		child.draw(display, gc, scale);
    		int childX = child.getPixelsX();
            childOffsetX += childX + m_connectorLen;
    	}
	}

	public void annotateXy(int xOffset, int yOffset) {
		if (DEBUG) System.out.printf("Annotate StructureFunction (%d, %d)\n", xOffset, yOffset);
        m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;

    	int childOffsetX = m_borderX;
    	int childrenY = getChildrenY();
		for (ViewNode child : getChildren()) {
			int childY = child.getPixelsY();
    		child.annotateXy(xOffset + childOffsetX, yOffset + m_borderY + ((childrenY - childY) / 2));
            childOffsetX += child.getPixelsX() + m_connectorLen;
    	}
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
		for (ViewNode child : getChildren()) {
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
