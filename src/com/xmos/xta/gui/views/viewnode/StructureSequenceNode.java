package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class StructureSequenceNode extends StructureNode {
    public static final boolean DEBUG = false;

	public StructureSequenceNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	    m_borderX = 4;
	    m_borderY = 2;
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

	public void draw(Display display, GC gc) {
		if (!isVisible())
			return;

		drawThis(display, gc);
		drawSequenceChildren(display, gc);
    }

	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureSequence (%d, %d)\n", offsetX, offsetY);
		m_pixelOffsetX = offsetX;
		m_pixelOffsetY = offsetY;

    	int childOffsetX = m_borderX;
    	int childrenY = getChildrenY();
		for (ViewNode child : getChildren()) {
			int childY = child.getPixelsY();
    		child.annotateNodes(offsetX + childOffsetX, offsetY + m_borderY + ((childrenY - childY) / 2),
    				isBestCase, isWorstCase);
            childOffsetX += child.getPixelsX() + m_connectorLen;
    	}

		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}
}
