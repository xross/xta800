package com.xmos.xta.gui.views.structure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.views.ZoomedCanvasView;
import com.xmos.xta.gui.views.viewnode.StructureBlockNode;
import com.xmos.xta.gui.views.viewnode.StructureFunctionNode;
import com.xmos.xta.gui.views.viewnode.StructureLoopNode;
import com.xmos.xta.gui.views.viewnode.StructurePathNode;
import com.xmos.xta.gui.views.viewnode.StructureSectionNode;
import com.xmos.xta.gui.views.viewnode.ViewNode;

public class StructureView extends ZoomedCanvasView {

	public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.structure.Structure";

    private ViewNode m_viewNode;
    
	public StructureView() {
		super();
		if (DEBUG) System.out.printf("Structure()\n");			
		GuiEngine.addBroadcastListener(this);

		m_scaleY = true;
	}

    protected void buildClickAreas() {
    	m_clickAreas.clear();
    	if (m_viewNode != null) {
    		m_viewNode.buildClickAreas(m_clickAreas);
    	}
    }

    protected void annotateTree() {
		if (m_viewNode != null) {
			m_viewNode.annotateXy(0, 0);
			updatePixelsXy();
		}
    }

    protected void updatePixelsXy() {
    	if (m_doubleClickNode == null) {
    		m_canvasProperties.setPixels(0, 0);
		} else {
    		m_canvasProperties.setPixels(m_viewNode.getPixelsX(), m_viewNode.getPixelsY());
		}
    }
    
    protected void drawCanvas(GC gc) {
		Rectangle clientArea = m_canvas.getClientArea();
		int renderX = clientArea.width;
		int renderY = clientArea.height;
        Image bufferedImage = new Image(m_display, renderX, renderY);
        GC bufferedGc = new GC(bufferedImage);
        
        bufferedGc.setAntialias(SWT.ON);
        bufferedGc.setTextAntialias(SWT.ON);

        bufferedGc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        bufferedGc.fillRectangle(0, 0, renderX, renderY);

        if (m_doubleClickNode != null) {
        	int offsetX = m_hScroll.getSelection();
        	int offsetY = m_vScroll.getSelection();
        	Rectangle visibleArea = new Rectangle(offsetX, offsetY, renderX, renderY);
        	m_canvasProperties.setVisibleArea(visibleArea);
        	m_canvasProperties.setScale(m_scale);
        	m_canvasProperties.setSelected(m_selectedNodes);
        	
        	if (DEBUG) System.out.println("Draw canvas rectangle " + offsetX + ", " + offsetY + " in area " + renderX + ", " + renderY);
			m_viewNode.draw(m_display, bufferedGc, true, true);
        }
        gc.drawImage(bufferedImage, 0, 0);
        bufferedGc.dispose();
        bufferedImage.dispose();
		buildClickAreas();
    }
    
	private ViewNode buildViewNode(Node node, int depth) {
		if (node == null || node.isNullNode())
			return null;
		
    	ViewNode viewNode = null;
    	
    	if (node.isBlockNode()) {
    		viewNode = new StructureBlockNode(node, m_canvasProperties);
    	} else if (node.isFunctionNode()) {
    		viewNode = new StructureFunctionNode(node, m_canvasProperties);
    		if (depth > 3)
    			viewNode.setFolded(true, false);
    	} else if (node.isLoopNode()) {
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

    protected void nodeCoreChanged() {
		if (DEBUG) System.out.printf("Structure: nodeCoreChanged\n");
	}

    protected void initialiseTree() {
		if (DEBUG) System.out.printf("Structure: initialiseTree\n");
		Route route = GuiEngine.getEngine().getSelectedRoute();
		if (route != null) {
			m_rootNode = route.getNode();
			m_doubleClickNode = m_rootNode;
			m_selectedNodes = GuiEngine.getSelectedNodes();
			m_viewNode = buildViewNode(m_doubleClickNode, 0);
			annotateTree();
		} else {
			m_rootNode = null;
			m_doubleClickNode = null;
			m_selectedNodes.clear();
		}
	}
}
