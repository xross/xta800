package com.xmos.xta.gui.views.viewnode;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class FunctionPathNode extends FunctionNode {
    public static final boolean DEBUG = false;

	public FunctionPathNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}

	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate FunctionLoop (%d, %d\n", offsetX, offsetY);
	}

	public ViewNode getClickNode(int x, int y) {
		// Don't let the hover-over select a path
		if (this.contains(x, y)) {
			for (ViewNode child : getChildren()) {
				ViewNode clickNode = child.getClickNode(x, y);
				if (clickNode != null)
					return clickNode;
	    	}
		}
		return null;
	}
}
