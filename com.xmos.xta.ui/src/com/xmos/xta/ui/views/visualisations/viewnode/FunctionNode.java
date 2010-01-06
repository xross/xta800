package com.xmos.xta.ui.views.visualisations.viewnode;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;

public abstract class FunctionNode extends ViewNode {
    public static final int TEXT_AREA_Y = 20;

    public FunctionNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
    	super(treeNode, canvasProperties);
    }

    public void draw(Display display, GC gc) {
		assert(false);
	}

	public int getPixelsX() {
		return getPixelsX(Case.WORST);
	}
	
	public int getPixelsX(Case c) {
		return getChildrenX(c);
	}

	public int getPixelsY() {
		int pixelsY = 0;
		pixelsY += getPixelsY(Case.WORST);
		pixelsY += getPixelsY(Case.BEST);
		return pixelsY;
	}
	
	public int getPixelsY(Case c) {
		int pixels = 0;
		for (ViewNode child : getChildren()) {
			int childPixels = child.getPixelsY(c);
			if (childPixels > pixels)
				pixels = childPixels;
		}
		return pixels;
    }

	public int getChildrenX() {
		assert(false);
		return 0;
	}

	public int getChildrenX(Case c) {
		int pixels = 0;
		for (ViewNode child : getChildren()) {
			pixels += child.getPixelsX(c);
		}
		return pixels;
	}

	public int getChildrenY() {
		assert(false);
		return 0;
	}
	
	public int getChildrenY(Case c) {
		int pixels = 0;
		for (ViewNode child : getChildren()) {
			int childPixels = child.getPixelsY(c);
			if (childPixels > pixels)
				pixels = childPixels;
		}
		return pixels;
	}

	public double draw(Display display, GC gc, Case c, double x, double y) {
		for (ViewNode child : getChildren()) {
    		x = child.draw(display, gc, c, x, y);
    	}
		return x;
	}

	public ViewNode getClickNode(int x, int y) {
		assert(false); // Need a case for function nodes
		return null;
	}

	public ViewNode getClickNode(int x, int y, Case c) {
		for (ViewNode child : getChildren()) {
			ViewNode node = child.getClickNode(x, y, c);
			if (node != null) {
				return node;
			}

			x -= child.getPixelsX(c);
			if (x < 0) {
				return null;
			}
    	}
		return null;
	}

	public boolean contains(int x, int y) {
		// The function view doesn't allow clicking
		return false;
	}

	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		// Do nothing
	}

	public void updateScaledXy() {
		// Do nothing
	}
	
    public int getPixelOffsetX() {
    	assert(false);
    	return 0;
    }

    public int getPixelOffsetY() {
    	assert(false);
    	return 0;
    }

    public boolean isBestCase() {
		return false;
	}

    public boolean isWorstCase() {
		return false;
	}

}
