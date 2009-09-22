package com.xmos.xta.ui.views.viewnode;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.views.ZoomedCanvasProperties;

public class FunctionLoopNode extends FunctionNode {
    public static final boolean DEBUG = false;
    
	public FunctionLoopNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}
}
