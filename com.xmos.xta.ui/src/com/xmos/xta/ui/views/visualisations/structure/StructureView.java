package com.xmos.xta.ui.views.visualisations.structure;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.visualisations.ControlMonitor;
import com.xmos.xta.ui.views.visualisations.Renderer;
import com.xmos.xta.ui.views.visualisations.RendererControl;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasView;
import com.xmos.xta.ui.views.visualisations.viewnode.StructureBlockNode;
import com.xmos.xta.ui.views.visualisations.viewnode.StructureConditionalNode;
import com.xmos.xta.ui.views.visualisations.viewnode.StructureFunctionNode;
import com.xmos.xta.ui.views.visualisations.viewnode.StructureLoopNode;
import com.xmos.xta.ui.views.visualisations.viewnode.StructureSequenceNode;
import com.xmos.xta.ui.views.visualisations.viewnode.ViewNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class StructureView extends ZoomedCanvasView implements ControlMonitor {
    public static final String ID = "com.xmos.xta.ui.views.structure.Structure";

	public StructureView(Composite parent) {
		super("Structure", parent);
	}

	protected ViewNode buildViewNode(Node node, int depth) {
		if (node == null || node.isNullNode())
			return null;
		
    	ViewNode viewNode = null;
    	
    	if (node.isBlockNode()) {
    		viewNode = new StructureBlockNode(node, m_canvasProperties);
    	} else if (node.isFunctionNode()) {
    		viewNode = new StructureFunctionNode(node, m_canvasProperties);
    		depth += 1;
    		if (depth > 2)
    			viewNode.setFolded(true, false);
    	} else if (node.isLoopNode() || node.isSelfLoopNode()) {
    		viewNode = new StructureLoopNode(node, m_canvasProperties, false);
    	} else if (node.isInfiniteLoopNode() || node.isInfiniteSelfLoopNode()) {
    		viewNode = new StructureLoopNode(node, m_canvasProperties, true);
    	} else if (node.isCondNode()) {
    		viewNode = new StructureConditionalNode(node, m_canvasProperties);
    	} else {
    		viewNode = new StructureSequenceNode(node, m_canvasProperties);
    	}
    	
    	if (!node.isBlockNode()) {
	        for (Node subNode : node.getIndirectChildren()) {
	        	ViewNode child = buildViewNode(subNode, depth);
	        	if (child != null)
	        		viewNode.addChild(child);
	        }
    	}

    	return viewNode;
	}

	protected RendererControl createRenderControl(Display display) {
		return new RendererControl(getName(), display, 2, 2, 2, 2);
	}

	protected Renderer createRenderer(Display display, RendererControl renderControl, 
			ZoomedCanvasProperties canvasProperties, Canvas canvas) {
		return new StructureRenderer(display, renderControl, canvasProperties, canvas);
	}

	protected ZoomedCanvasProperties createCanvasProperties() {
		return new ZoomedCanvasProperties(true);
	}
	
	public void setExportJpgEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_EXPORT_JPG_ENABLED, "");
	}

	public void setZoomFitEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_FIT_ENABLED, "");
	}

	public void setZoomInEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_IN_ENABLED, "");
	}

	public void setZoomOutEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_OUT_ENABLED, "");
	}

	public void setZoomResetEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_RESET_ENABLED, "");
	}
}
