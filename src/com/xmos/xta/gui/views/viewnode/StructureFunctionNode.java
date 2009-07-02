package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.structure.ZoomedCanvasProperties;

public class StructureFunctionNode extends ViewNode {
    public static final boolean DEBUG = false;
    private static final int NAME_PADDING = 5;

    private int m_borderX = 5;
    private int m_borderY = 5;
    private int m_textAreaX = 0;
    private int m_textAreaY = 20;

	public StructureFunctionNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
		m_textAreaX = canvasProperties.getAverageCharWidth() * treeNode.getFunctionName().length() + NAME_PADDING;
	}

	public int getPixelsX() {
		if (isFolded()) {
			return (2 * m_borderX) + m_textAreaX;
		} else {
			return getChildrenX() + (2 * m_borderX) + m_textAreaX;
		}
	}

	public int getPixelsY() {
		if (isFolded()) {
			return (2 * m_borderY) + m_textAreaY;
		} else {
			return getChildrenY() + (2 * m_borderY) + m_textAreaY;
		}
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

	public void draw(Display display, GC structureGc, GC timesGc) {
		// Don't currently support connecting up nodes like a path
		assert(getChildren().size() == 1);
        drawThis(display, structureGc);

    	structureGc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	if (isSelected()) {
    		structureGc.setLineWidth(getLineWidth() + 1);
           	structureGc.drawRectangle(m_x + 1, m_y + 1, m_width - 1, m_height - 1);
    		structureGc.setLineWidth(getLineWidth());
    	} else {
    		structureGc.drawRectangle(m_x, m_y, m_width, m_height);
    	}

       	if (!isFolded()) {
    		int childRightX = 0;
            int childMidY = 0;
			for (ViewNode child : getChildren()) {
	    		child.draw(display, structureGc, timesGc);

	    		childRightX = scale(child.getPixelOffsetX() + child.getPixelsX());
	    		childMidY = scale(child.getPixelOffsetY() + (child.getPixelsY() / 2));
	    	}

	    	int midY = scale(m_pixelOffsetY + (getPixelsY() / 2));
	    	int childLeftX = scale(m_pixelOffsetX + m_borderX + m_textAreaX);

    		boolean isWorstCase = isWorstCase();
    		boolean isBestCase = isBestCase();
    		drawConnection(display, structureGc, m_x, midY, childLeftX, midY, isWorstCase, isBestCase);
			drawConnection(display, structureGc, childLeftX, midY, childLeftX, childMidY, isWorstCase, isBestCase);

			drawConnection(display, structureGc, childRightX, childMidY, childRightX, midY, isWorstCase, isBestCase);
			drawConnection(display, structureGc, childRightX, midY, scale(m_pixelOffsetX + getPixelsX()), midY, isWorstCase, isBestCase);
       	} else {
       		drawTimes(display, timesGc);
       	}
		
		// Draw the function name
		if (scale(m_height) > structureGc.getFontMetrics().getHeight() &&
			scale(m_width) > m_textAreaX) {
	        int textBase = scale(m_pixelOffsetY + m_borderY);
			structureGc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			structureGc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

			structureGc.drawString(m_treeNode.getFunctionName(),  m_x + m_borderX, textBase);
		}
	}

	public void annotateNodes(int xOffset, int yOffset, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureFunction (%d, %d)\n", xOffset, yOffset);
        m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;

    	int childrenY = getChildrenY();
    	int childOffsetY = yOffset + m_borderY + m_textAreaY;
		for (ViewNode child : getChildren()) {
			int childY = child.getPixelsY();
    		child.annotateNodes(xOffset + m_borderX + m_textAreaX, childOffsetY + ((childrenY - childY) / 2),
    				isBestCase, isWorstCase);
    	}
		
		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}
}
