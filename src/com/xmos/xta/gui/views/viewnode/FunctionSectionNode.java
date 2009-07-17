package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class FunctionSectionNode extends FunctionNode {
    public static final boolean DEBUG = false;

	public FunctionSectionNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}

	public int getPixelsY(Case c) {
		ViewNode child = getChild(Case.WORST);
		if (child != null) {
			return child.getPixelsY(c);
		}
		return 0;
	}

	public int getChildrenX() {
		ViewNode child = getChild(Case.WORST);
		if (child != null) {
			return child.getPixelsX();
		}
		return 0;
	}

	public int getChildrenY() {
		int pixelsY = 0;
		pixelsY += getPixelsY(Case.BEST);
		pixelsY += getPixelsY(Case.WORST);
		return pixelsY;
	}

	public double draw(Display display, GC gc, Case c, double x, double y) {
		ViewNode child = getChild(c);
		if (child != null) {
			return child.draw(display, gc, c, x, y);
		}
		return x;
	}

	public ViewNode getClickNode(int x, int y) {
		ViewNode child = getChild(Case.BEST);
		if (child != null) {
			ViewNode clickNode = child.getClickNode(x, y);
			if (clickNode != null)
				return clickNode;
		}
		child = getChild(Case.BEST);
		if (child != null) {
			ViewNode clickNode = child.getClickNode(x, y);
			if (clickNode != null)
				return clickNode;
		}
		return null;
	}
}
