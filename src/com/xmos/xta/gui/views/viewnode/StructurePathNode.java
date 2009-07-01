package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.structure.ZoomedCanvasProperties;

public class StructurePathNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_connectorLen = 7;
    private int m_borderX = 4;
    private int m_borderY = 2;

	public StructurePathNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
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

	public void draw(Display display, GC structureGc, GC timesGc) {
        drawThis(display, structureGc);
		boolean isWorstCase = isWorstCase();
		boolean isBestCase = isBestCase();

        boolean first = true;
    	int childOffsetX = m_borderX;
    	int lineY = scale(m_pixelOffsetY + (getPixelsY() / 2));
    	int lineLeft = 0;
    	int lineRight = 0;
		for (ViewNode child : getChildren()) {
    		int childX = child.getPixelsX();
    		
        	if (!first) {
        		drawConnection(display, structureGc, lineLeft, lineY, lineRight, lineY, isWorstCase, isBestCase);
        	} else {
        		first = false;
        	}

            lineLeft = scale(m_pixelOffsetX + childOffsetX + childX);
            lineRight = scale(m_pixelOffsetX + childOffsetX + childX + m_connectorLen);
            childOffsetX += childX + m_connectorLen;

    		child.draw(display, structureGc, timesGc);
		}

		drawConnection(display, structureGc, m_x, lineY, scale(m_pixelOffsetX + m_borderX), lineY, isWorstCase, isBestCase);
		drawConnection(display, structureGc, lineLeft, lineY, scale(m_pixelOffsetX + getPixelsX()), lineY, isWorstCase, isBestCase);
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

	public boolean isFolded() {
		return false;
	}
}
