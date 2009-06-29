package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

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

	public void draw(Display display, GC gc, boolean isWorstCase, boolean isBestCase) {
		// Don't currently support connecting up nodes like a path
		assert(getChildren().size() == 1);
		setXy();
		if (!isVisible()) {
			return;
		}
        drawThis(display, gc);

    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	if (isSelected()) gc.setLineWidth(getLineWidth() + 1);
       	gc.drawRectangle(m_x, m_y, m_width, m_height);
       	if (isSelected()) gc.setLineWidth(getLineWidth());

       	if (!isFolded()) {
            int childRightX = 0;
            int childMidY = 0;
			for (ViewNode child : getChildren()) {
	    		child.draw(display, gc, isWorstCase, isBestCase);

	    		childRightX = scaleX(child.getPixelOffsetX() + child.getPixelsX());
	    		childMidY = scaleY(child.getPixelOffsetY() + (child.getPixelsY() / 2));
	    	}

	    	int midY = scaleY(m_pixelOffsetY + (getPixelsY() / 2));
	    	int childLeftX = scaleX(m_pixelOffsetX + m_borderX + m_textAreaX);
			drawConnection(display, gc, m_x, midY, childLeftX, midY, isWorstCase, isBestCase);
			drawConnection(display, gc, childLeftX, midY, childLeftX, childMidY, isWorstCase, isBestCase);

			drawConnection(display, gc, childRightX, childMidY, childRightX, midY, isWorstCase, isBestCase);
			drawConnection(display, gc, childRightX, midY, scaleX(m_pixelOffsetX + getPixelsX()), midY, isWorstCase, isBestCase);
       	}
		
		// Draw the function name
		if (m_height > gc.getFontMetrics().getHeight()) {
	        int textBase = scaleY(m_pixelOffsetY + m_borderY);
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

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
    		child.annotateXy(xOffset + m_borderX + m_textAreaX, childOffsetY + ((childrenY - childY) / 2));
    	}
	}
}
