package com.xmos.xta.ui.views.visualisations.viewnode;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;

public class FunctionSequenceNode extends FunctionNode {
    public static final boolean DEBUG = false;

	public FunctionSequenceNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}
}
