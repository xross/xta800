package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class StructureFunctionNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 5;
    private int m_borderY = 5;
    private int m_textAreaY = 20;

	public StructureFunctionNode(Node treeNode) {
		super(treeNode);
	}

	public int getPixelsX() {
		return getChildrenX() + (2 * m_borderX);
	}

	public int getPixelsY() {
		return getChildrenY() + (2 * m_borderY) + m_textAreaY;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (ViewNode child : getChildren()) {
			pixels += child.getPixelsX();
		}
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

	public void draw(Display display, GC gc, double scale, Rectangle renderArea, boolean isWorstCase) {
		// Don't currently support connecting up nodes like a path
		assert(getChildren().size() == 1);
		setXy(scale, renderArea);
        drawThis(display, gc);

    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	if (m_isSelected) gc.setLineWidth(m_lineWidth + 1);
       	gc.drawRectangle(m_x, m_y, m_width, m_height);
       	if (m_isSelected) gc.setLineWidth(m_lineWidth);

		for (ViewNode child : getChildren()) {
    		child.draw(display, gc, scale, renderArea, isWorstCase);
    	}
		
		// Draw the function name
        int textBase = scaleY(m_pixelOffsetY + m_borderY);
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        int functionNameWidth = gc.getFontMetrics().getAverageCharWidth() * m_treeNode.getFunctionName().length();
        if (m_width >= functionNameWidth) {
        	gc.drawString(m_treeNode.getFunctionName(),  m_x + m_borderX, textBase);
        }
	}

	public void annotateXy(int xOffset, int yOffset) {
		if (DEBUG) System.out.printf("Annotate StructureFunction (%d, %d)\n", xOffset, yOffset);
        m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;

    	int childrenY = getChildrenY();
    	int childOffsetY = yOffset + m_borderY + m_textAreaY;
		for (ViewNode child : getChildren()) {
			int childY = child.getPixelsY();
    		child.annotateXy(xOffset + m_borderX, childOffsetY + ((childrenY - childY) / 2));
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
