package com.xmos.xta.ui.views.visualisations.viewnode;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;

public class FunctionConditionalNode extends FunctionNode {
    public static final boolean DEBUG = false;

	public FunctionConditionalNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}

	public int getPixelsY(Case c) {
		ViewNode child = getChild(c);
		if (child != null) {
			return child.getPixelsY(c);
		}
		return 0;
	}

	public int getChildrenX(Case c) {
		ViewNode child = getChild(c);
		if (child != null) {
			return child.getPixelsX(c);
		}
		return 0;
	}

	public int getChildrenY(Case c) {
		int pixelsY = getPixelsY(c);
		return pixelsY;
	}

	public double draw(Display display, GC gc, Case c, double x, double y) {
		ViewNode child = getChild(c);
		if (child != null) {
			return child.draw(display, gc, c, x, y);
		}
		return x;
	}

	public ViewNode getClickNode(int x, int y, Case c) {
		ViewNode child = getChild(c);
		if (child != null) {
			ViewNode clickNode = child.getClickNode(x, y, c);
			if (clickNode != null)
				return clickNode;
		}
		return null;
	}
}
