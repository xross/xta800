package com.xmos.xta.ui.views.visualisations.function;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.visualisations.Renderer;
import com.xmos.xta.ui.views.visualisations.RendererControl;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasView;
import com.xmos.xta.ui.views.visualisations.viewnode.FunctionBlockNode;
import com.xmos.xta.ui.views.visualisations.viewnode.FunctionConditionalNode;
import com.xmos.xta.ui.views.visualisations.viewnode.FunctionFunctionNode;
import com.xmos.xta.ui.views.visualisations.viewnode.FunctionLoopNode;
import com.xmos.xta.ui.views.visualisations.viewnode.FunctionNode;
import com.xmos.xta.ui.views.visualisations.viewnode.FunctionSequenceNode;
import com.xmos.xta.ui.views.visualisations.viewnode.ViewNode;

public class FunctionView extends ZoomedCanvasView {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.ui.views.function.Function";

    private static final int TIMELINE_Y = 15;
    private static final int BORDER_Y = 10;

	public FunctionView(Composite parent) {
		super("Function", parent);
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
    		depth += 1;
    		if (depth > 2)
    			viewNode.setFolded(true, false);
    	} else if (node.isLoopNode() || node.isSelfLoopNode()) {
    		viewNode = new FunctionLoopNode(node, m_canvasProperties);
    	} else if (node.isCondNode()) {
    		viewNode = new FunctionConditionalNode(node, m_canvasProperties);
    	} else {
    		viewNode = new FunctionSequenceNode(node, m_canvasProperties);
    	}
    	
    	if (!node.isBlockNode()) {
	        for (Node subNode : node.iChildren()) {
	        	ViewNode child = buildViewNode(subNode, depth);
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

	public void menuAboutToShow(IMenuManager manager) {
		// Do nothing
	}

	protected ViewNode getClickNode(int x, int y) {
    	if (m_viewNode != null) {
			int offsetX = m_renderControl.getOffsetX();
        	x = (int)((double)(x + offsetX) / m_renderControl.getScale());

			int offsetY = m_renderControl.getOffsetY();
			y = y + offsetY - TIMELINE_Y;
			
        	int middleY = 0;
        	middleY += m_viewNode.getPixelsY(Case.WORST) + FunctionRenderer.BEST_WORST_SEPARATION;
        	
        	ViewNode viewNode = null;
        	if (y >= middleY) {
        		viewNode = m_viewNode.getClickNode(x, y - middleY, Case.BEST);
        	} else {
        		viewNode = m_viewNode.getClickNode(x, y, Case.WORST);
        	}
        	
        	if ((viewNode != null) && viewNode.getTreeNode().isFunctionNode()) {
        		return viewNode;
        	}
    	}
		return null;
	}
}
