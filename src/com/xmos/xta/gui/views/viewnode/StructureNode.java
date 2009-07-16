package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.gui.icons.Icons;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public abstract class StructureNode extends ViewNode {
    static final Image m_warningImage = Icons.getIcon("warning");
    static final int m_warningX = m_warningImage.getBounds().width;
    static final int m_warningY = m_warningImage.getBounds().height;

    int m_connectorLen = 7;
    int m_borderX;
    int m_borderY;

    public StructureNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
    	super(treeNode, canvasProperties);
    }

    public double draw(Display display, GC gc, Case c, double offsetX, double offsetY) {
    	assert(false);
    	return 0;
    }

    public int getPixelsY(Case c) {
    	assert(false);
    	return 0;
    }

    protected void drawPathChildren(Display display, GC gc) {
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
	    m_treeNode.traverse(0, new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, int depth) {
	    		NodeAttributes nodeAttributes = node.getAttributes();		    		
	    		if (nodeAttributes.isUnknown() &&
	    			(nodeAttributes.getUnknownState() == UnknownState.UNKNOWN)) {
	    			found = true;
	    			return false;
	    		}
	        	return true;
	    	}
	    });
		return found;
	}
}
