package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class StructureFunctionNode extends StructureNode {
    public static final boolean DEBUG = false;

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

	public void draw(Display display, GC gc) {
		if (!isVisible())
			return;
		
		// Don't currently support connecting up nodes like a path
		assert(getChildren().size() == 1);
        drawThis(display, gc);

    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	if (isSelected()) {
    		gc.setLineWidth(getLineWidth() + 1);
           	gc.drawRectangle(m_renderX + 1, m_renderY + 1, (int)m_width - 1, (int)m_height - 1);
    		gc.setLineWidth(getLineWidth());
    	} else {
    		gc.drawRectangle(m_renderX, m_renderY, (int)m_width, (int)m_height);
    	}

       	if (!isFolded()) {
    		boolean isWorstCase = isWorstCase();
    		boolean isBestCase = isBestCase();
       		if ((getChildren().size() > 0)) {
	    		int childRightX = 0;
	            int childMidY = 0;
				for (ViewNode child : getChildren()) {
		    		child.draw(display, gc);
	
		    		childRightX = scaleX(child.getPixelOffsetX() + child.getPixelsX());
		    		childMidY = scaleY(child.getPixelOffsetY() + (child.getPixelsY() / 2));
		    	}
	
		    	int midY = scaleY(m_pixelOffsetY + (getPixelsY() / 2));
		    	int childLeftX = scaleX(m_pixelOffsetX + m_borderX + m_textAreaX);
	
	    		drawConnection(display, gc, m_renderX, midY, childLeftX, midY,
	    				isBestCase, isWorstCase);
				drawConnection(display, gc, childLeftX, midY, childLeftX, childMidY,
						isBestCase, isWorstCase);
	
				drawConnection(display, gc, childRightX, childMidY, childRightX, midY,
						isBestCase, isWorstCase);
				drawConnection(display, gc, childRightX, midY, scaleX(m_pixelOffsetX + getPixelsX()), midY,
						isBestCase, isWorstCase);
       		} else {
		    	int midY = scaleY(m_pixelOffsetY + (getPixelsY() / 2));
	    		drawConnection(display, gc, m_renderX, midY, scaleX(m_pixelOffsetX + getPixelsX()), midY,
	    				isBestCase, isWorstCase);
       		}
       	}
		
		// Draw the function name
		if (scale((int)m_height) > gc.getFontMetrics().getHeight() &&
			scale((int)m_width) > m_textAreaX) {
	        int textBase = scaleY(m_pixelOffsetY + m_borderY);
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

			gc.drawString(m_treeNode.getFunctionName(),  m_renderX + m_borderX, textBase);
		}
	}

	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureFunction (%d, %d)\n", offsetX, offsetY);
        m_pixelOffsetX = offsetX;
		m_pixelOffsetY = offsetY;

    	int childrenY = getChildrenY();
    	int childOffsetY = offsetY + m_borderY + m_textAreaY;
		for (ViewNode child : getChildren()) {
			int childY = child.getPixelsY();
    		child.annotateNodes(offsetX + m_borderX + m_textAreaX, childOffsetY + ((childrenY - childY) / 2),
    				isBestCase, isWorstCase);
    	}
		
		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}
}
