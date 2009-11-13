package com.xmos.xta.ui.views.visualisations.viewnode;

import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.ui.icons.Icons;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;

public abstract class StructureNode extends ViewNode {
    static final Image m_warningImage = Icons.getIcon("warning");
    static final int m_warningX = m_warningImage.getBounds().width;
    static final int m_warningY = m_warningImage.getBounds().height;

    int m_connectorLen = 7;
    int m_borderX;
    int m_borderY;

    int m_pixelOffsetX;
    int m_pixelOffsetY;
    int m_renderX;
    int m_renderY;
    
    int m_width;
    int m_height;

    boolean m_isBestCase;
    boolean m_isWorstCase;

    public StructureNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
    	super(treeNode, canvasProperties);

	    m_isBestCase = false;
	    m_isWorstCase = false;

	    m_pixelOffsetX = 0;
	    m_pixelOffsetY = 0;
    }

    public double draw(Display display, GC gc, Case c, double offsetX, double offsetY) {
    	assert(false);
    	return 0;
    }

    public int getPixelsY(Case c) {
    	assert(false);
    	return 0;
    }

    protected void drawSequenceChildren(Display display, GC gc) {
		boolean isWorstCase = isWorstCase();
		boolean isBestCase = isBestCase();
		boolean isInfinite = false;

		assert(getChildren().size() > 0);
    	ViewNode firstChild = getChildren().get(0);
		
		int midY = scaleY(m_pixelOffsetY + (getPixelsY() / 2));
    	int midX = getLeftMidX();
		int childMidY = scaleY(firstChild.getPixelOffsetY() + (firstChild.getPixelsY() / 2));
		int childLeftX = scaleX(firstChild.getPixelOffsetX());
    	drawConnection(display, gc, m_renderX, midY, midX, midY,
				isBestCase, isWorstCase);
		drawConnection(display, gc, midX, midY, midX, childMidY,
				isBestCase, isWorstCase);
		drawConnection(display, gc, midX, childMidY, childLeftX, childMidY,
				isBestCase, isWorstCase);

		boolean first = true;
    	int childOffsetX = firstChild.getPixelOffsetX();
    	
    	int lineLeft = 0;
    	int lineRight = 0;
		for (ViewNode child : getChildren()) {
    		int childX = child.getPixelsX();
        	if (!first && !isInfinite) {
        		drawConnection(display, gc, lineLeft, childMidY, lineRight, childMidY,
        				isBestCase, isWorstCase);
        	} else {
        		first = false;
        	}
    		isInfinite = child.isInfinite();

            lineLeft = scaleX(childOffsetX + childX);
            lineRight = scaleX(childOffsetX + childX + m_connectorLen);
            childOffsetX += childX + m_connectorLen;

    		child.draw(display, gc);
		}

		if (!isInfinite) {
			int right = m_pixelOffsetX + getPixelsX();
			int rightX = scaleX(right);
			int rightMidX = scaleX(right - (m_borderX / 2));
			drawConnection(display, gc, lineLeft, childMidY, rightMidX, childMidY,
				isBestCase, isWorstCase);
			drawConnection(display, gc, rightMidX, childMidY, rightMidX, midY,
					isBestCase, isWorstCase);
			drawConnection(display, gc, rightMidX, midY, rightX, midY,
					isBestCase, isWorstCase);
		}
	}

	int getLeftMidX() {
		return scaleX(m_pixelOffsetX + (m_borderX / 2));
	}

	private boolean found;
	protected boolean hasUnknowns() {
	    found = false;
	    m_treeNode.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
	    		if (node.getAttributes().hasUnknowns()) {
	    			found = true;
	    			return TraverseStatus.NO_CHILDREN;
	    		}
	        	return TraverseStatus.CONTINUE;
	    	}
	    });
		return found;
	}

	public void updateScaledXy() {
		int pixelsX = getPixelsX();
		int pixelsY = getPixelsY();
		
        m_renderX = scaleX(m_pixelOffsetX);
        m_renderY = scaleY(m_pixelOffsetY);
        m_width = (int)((double)pixelsX * m_canvasProperties.getScale());
        
        if (m_canvasProperties.scaleY()) {
        	m_height = (int)((double)pixelsY * m_canvasProperties.getScale());
        } else {
        	m_height = pixelsY;
        }

		for (ViewNode child : getChildren()) {
    		child.updateScaledXy();
		}
	}

	public boolean isVisible() {
		Rectangle visibleArea = m_canvasProperties.getVisibleArea();
		int x = scale(m_pixelOffsetX);
		int y = scale(m_pixelOffsetY);
		if (m_width < 1 || m_height < 1 ||
			(x + m_width < visibleArea.x) ||
			(x > visibleArea.x + visibleArea.width) ||
			(y + m_height < visibleArea.y) ||
			(y > visibleArea.y + visibleArea.height)) {
			return false;
		} else {
			return true;
		}
	}

	public void drawThis(Display display, GC gc) {
	    if (isSelected()) {
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
	    	gc.setAlpha(100);
	    	gc.fillRoundRectangle(m_renderX, m_renderY, (int)m_width, (int)m_height, CORNER_ROUNDING, CORNER_ROUNDING);
	    	gc.setAlpha(255);
	    } else {
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
	    	gc.fillRectangle(m_renderX, m_renderY, (int)m_width, (int)m_height);
	    }
	}

    public boolean isBestCase() {
		return m_isBestCase;
	}

    public boolean isWorstCase() {
		return m_isWorstCase;
	}

    public int getPixelOffsetX() {
    	return m_pixelOffsetX;
    }

    public int getPixelOffsetY() {
    	return m_pixelOffsetY;
    }

	public boolean contains(int x, int y) {
		int thisX = scale(m_pixelOffsetX);
		int thisY = scale(m_pixelOffsetY);
		int width = scale(getPixelsX());
		int height = scale(getPixelsY());
		if ((x >= thisX) && (x <= (thisX + width))) {
			if ((y >= thisY) && (y <= (thisY + height))) {
				return true;
			}
		}
		return false;
	}

	public ViewNode getClickNode(int x, int y) {
		if (this.contains(x, y)) {
			for (ViewNode child : getChildren()) {
				ViewNode clickNode = child.getClickNode(x, y);
				if (clickNode != null)
					return clickNode;
	    	}
			return this;
		} else {
			return null;
		}
	}
}
