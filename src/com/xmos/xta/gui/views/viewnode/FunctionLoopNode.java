package com.xmos.xta.gui.views.viewnode;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class FunctionLoopNode extends FunctionNode {
    public static final boolean DEBUG = false;
    
	public FunctionLoopNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}
	
	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate FunctionLoop (%d, %d\n", offsetX, offsetY);
	}
}
