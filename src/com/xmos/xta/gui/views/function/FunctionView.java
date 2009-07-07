package com.xmos.xta.gui.views.function;

import org.eclipse.swt.widgets.Display;

import com.xmos.xta.gui.views.Renderer;
import com.xmos.xta.gui.views.RendererControl;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;
import com.xmos.xta.gui.views.ZoomedCanvasView;
import com.xmos.xta.gui.views.viewnode.FunctionBlockNode;
import com.xmos.xta.gui.views.viewnode.FunctionFunctionNode;
import com.xmos.xta.gui.views.viewnode.FunctionLoopNode;
import com.xmos.xta.gui.views.viewnode.FunctionNode;
import com.xmos.xta.gui.views.viewnode.FunctionPathNode;
import com.xmos.xta.gui.views.viewnode.FunctionSectionNode;
import com.xmos.xta.gui.views.viewnode.ViewNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class FunctionView extends ZoomedCanvasView {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.function.Function";

    private static final int TIMELINE_Y = 15;
    private static final int BORDER_Y = 10;

	public FunctionView() {
		super("Function");
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
    	} else if (node.isSectionNode()) {
    		viewNode = new FunctionSectionNode(node, m_canvasProperties);
    	} else {
    		viewNode = new FunctionPathNode(node, m_canvasProperties);
    	}
    	
    	if (!node.isBlockNode()) {
	        for (Node subNode : node.getChildren()) {
	        	ViewNode child = buildViewNode(subNode, depth + 1);
	        	if (child != null)
	        		viewNode.addChild(child);
	        }
    	}

    	return viewNode;
	}

	protected RendererControl createRenderControl(Display display) {
		return new RendererControl(display, 2, BORDER_Y, 2, TIMELINE_Y + FunctionNode.TEXT_AREA_Y);
	}

	protected Renderer createRenderer(Display display, RendererControl renderControl, 
			ZoomedCanvasProperties canvasProperties) {
		return new FunctionRenderer(display, renderControl, canvasProperties, TIMELINE_Y, BORDER_Y);
	}

	protected ZoomedCanvasProperties createCanvasProperties() {
		return new ZoomedCanvasProperties(false);
	}
}
