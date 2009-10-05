package com.xmos.xta.ui.views.function;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.Renderer;
import com.xmos.xta.ui.views.RendererControl;
import com.xmos.xta.ui.views.ZoomedCanvasProperties;
import com.xmos.xta.ui.views.ZoomedCanvasView;
import com.xmos.xta.ui.views.viewnode.FunctionBlockNode;
import com.xmos.xta.ui.views.viewnode.FunctionFunctionNode;
import com.xmos.xta.ui.views.viewnode.FunctionLoopNode;
import com.xmos.xta.ui.views.viewnode.FunctionNode;
import com.xmos.xta.ui.views.viewnode.FunctionSequenceNode;
import com.xmos.xta.ui.views.viewnode.FunctionConditionalNode;
import com.xmos.xta.ui.views.viewnode.ViewNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class FunctionView extends ZoomedCanvasView {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.ui.views.function.Function";

    private static final int TIMELINE_Y = 15;
    private static final int BORDER_Y = 10;

	public FunctionView() {
		super("Function");
	}

	public void dispose() {
		GuiEngine.removeBroadcastListener(this);
		super.dispose();
	}

	protected ViewNode buildViewNode(Node node, int depth) {
		if (node == null || node.isNullNode())
			return null;
		
    	ViewNode viewNode = null;
    	
    	if (node.isBlockNode()) {
    		viewNode = new FunctionBlockNode(node, m_canvasProperties);
    	} else if (node.isFunctionNode()) {
    		viewNode = new FunctionFunctionNode(node, m_canvasProperties);
    		if (depth > 3)
    			viewNode.setFolded(true, false);
    	} else if (node.isLoopNode() || node.isSelfLoopNode()) {
    		viewNode = new FunctionLoopNode(node, m_canvasProperties);
    	} else if (node.isCondNode()) {
    		viewNode = new FunctionConditionalNode(node, m_canvasProperties);
    	} else {
    		viewNode = new FunctionSequenceNode(node, m_canvasProperties);
    	}
    	
    	if (!node.isBlockNode()) {
	        for (Node subNode : node.getIndirectChildren()) {
	        	ViewNode child = buildViewNode(subNode, depth + 1);
	        	if (child != null)
	        		viewNode.addChild(child);
	        }
    	}

    	return viewNode;
	}

	protected RendererControl createRenderControl(Display display) {
		return new RendererControl(getName(), display, 2, TIMELINE_Y, 2, FunctionNode.TEXT_AREA_Y * 2);
	}

	protected Renderer createRenderer(Display display, RendererControl renderControl, 
			ZoomedCanvasProperties canvasProperties, Canvas canvas) {
		return new FunctionRenderer(display, renderControl, canvasProperties, canvas, TIMELINE_Y, BORDER_Y);
	}

	protected ZoomedCanvasProperties createCanvasProperties() {
		return new ZoomedCanvasProperties(false);
	}
}
