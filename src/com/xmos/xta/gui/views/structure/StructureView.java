package com.xmos.xta.gui.views.structure;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.gui.views.Renderer;
import com.xmos.xta.gui.views.RendererControl;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;
import com.xmos.xta.gui.views.ZoomedCanvasView;
import com.xmos.xta.gui.views.viewnode.StructureBlockNode;
import com.xmos.xta.gui.views.viewnode.StructureFunctionNode;
import com.xmos.xta.gui.views.viewnode.StructureLoopNode;
import com.xmos.xta.gui.views.viewnode.StructurePathNode;
import com.xmos.xta.gui.views.viewnode.StructureSectionNode;
import com.xmos.xta.gui.views.viewnode.ViewNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class StructureView extends ZoomedCanvasView {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.structure.Structure";

	public StructureView() {
		super("Structure");
	}

	protected ViewNode buildViewNode(Node node, int depth) {
		if (node == null || node.isNullNode())
			return null;
		
    	ViewNode viewNode = null;
    	
    	if (node.isBlockNode()) {
    		viewNode = new StructureBlockNode(node, m_canvasProperties);
    	} else if (node.isFunctionNode()) {
    		viewNode = new StructureFunctionNode(node, m_canvasProperties);
    		if (depth > 3)
    			viewNode.setFolded(true, false);
    	} else if (node.isLoopNode() || node.isSelfLoopNode()) {
    		viewNode = new StructureLoopNode(node, m_canvasProperties);
    	} else if (node.isSectionNode()) {
    		viewNode = new StructureSectionNode(node, m_canvasProperties);
    	} else {
    		viewNode = new StructurePathNode(node, m_canvasProperties);
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

	protected RendererControl createRenderControl() {
		return new RendererControl(2, 2, 2, 2);
	}

	protected Renderer createRenderer(Display display, RendererControl renderControl, 
			ZoomedCanvasProperties canvasProperties, Canvas canvas) {
		return new StructureRenderer(display, renderControl, canvasProperties, canvas);
	}

	protected ZoomedCanvasProperties createCanvasProperties() {
		return new ZoomedCanvasProperties(true);
	}
}
