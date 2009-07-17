package com.xmos.xta.gui.views.viewnode;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class FunctionLoopNode extends FunctionNode {
    public static final boolean DEBUG = false;
    
	public FunctionLoopNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}
}
