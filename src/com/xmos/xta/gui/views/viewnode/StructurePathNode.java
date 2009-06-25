package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class StructurePathNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_connectorLen = 7;
    private int m_borderX = 4;
    private int m_borderY = 2;

	public StructurePathNode(Node treeNode) {
		super(treeNode);
	}

	public int getPixelsX() {
		return getChildrenX() + (2 * m_borderX);
	}

	public int getPixelsY() {
		return getChildrenY() + (2 * m_borderY);
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

	public void draw(Display display, GC gc, double scale, boolean isWorstCase) {
        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX, scale);
        m_y = scale(m_pixelOffsetY, scale);
        m_width = scale(getPixelsX(), scale);
        m_height = scale(getPixelsY(), scale);
        drawThis(display, gc);

        boolean first = true;
    	int childOffsetX = m_borderX;
    	int lineY = scale(m_pixelOffsetY + (getPixelsY() / 2), scale);
    	int lineLeft = 0;
    	int lineRight = 0;
		for (ViewNode child : getChildren()) {
    		int childX = child.getPixelsX();
    		
        	if (!first) {
            	setConnectionColour(display, gc, isWorstCase);
        		drawConnection(display, gc, lineLeft, lineY, lineRight, lineY);
        	} else {
        		first = false;
        	}

            lineLeft = scale(m_pixelOffsetX + childOffsetX + childX, scale);
            lineRight = scale(m_pixelOffsetX + childOffsetX + childX + m_connectorLen, scale);
            childOffsetX += childX + m_connectorLen;

    		child.draw(display, gc, scale, isWorstCase);
		}

    	setConnectionColour(display, gc, isWorstCase);
		drawConnection(display, gc, m_x, lineY, scale(m_pixelOffsetX + m_borderX, scale), lineY);
		drawConnection(display, gc, lineLeft, lineY, scale(m_pixelOffsetX + getPixelsX(), scale), lineY);
    }

	public void annotateXy(int xOffset, int yOffset) {
		if (DEBUG) System.out.printf("Annotate StructurePath (%d, %d)\n", xOffset, yOffset);

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
