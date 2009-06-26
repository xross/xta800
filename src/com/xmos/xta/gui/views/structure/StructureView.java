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
		m_pixelsX = 0;
		m_pixelsY = 0;
		m_borderX = 10;
		m_borderY = 10;
		m_lineWidth = 1;
	}

    protected void buildClickAreas() {
    	m_clickAreas.clear();
    	if (m_viewNode != null) {
    		m_viewNode.buildClickAreas(m_clickAreas);
    	}
    }

    protected void annotateTree(boolean isSelctionChange) {
		m_viewNode = buildViewNode(m_doubleClickNode);
    	buildClickAreas();
		
		// Build up a new list of click areas and then swap to ensure the old ones are available
		// for double clicks
		if (m_viewNode != null) {
			m_viewNode.annotateXy(m_borderX, m_borderY);
			updatePixelsXy();
		}
    }

    protected void updatePixelsXy() {
    	if (m_doubleClickNode == null) {
			m_pixelsX = 0;
			m_pixelsY = 0;
		} else {
			m_pixelsX = (m_borderX  * 2) + m_viewNode.getPixelsX();
			m_pixelsY = (m_borderY * 2) + m_viewNode.getPixelsY();
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
        	Rectangle renderArea = new Rectangle(offsetX, offsetY, renderX, renderY);
	        bufferedGc.setLineWidth(m_lineWidth);
			m_viewNode.draw(m_display, bufferedGc, m_scale, renderArea, true);
        }
        gc.drawImage(bufferedImage, 0, 0);
        bufferedGc.dispose();
        bufferedImage.dispose();
    }
    
	private ViewNode buildViewNode(Node node) {
		if (node == null || node.isNullNode())
			return null;
		
    	boolean selected = m_selectedNodes.contains(node);
    	ViewNode viewNode = null;
    	
    	if (node.isBlockNode()) {
    		viewNode = new StructureBlockNode(node);
    	} else if (node.isFunctionNode()) {
    		viewNode = new StructureFunctionNode(node);
    	} else if (node.isLoopNode()) {
    		viewNode = new StructureLoopNode(node);
    	} else if (node.isSectionNode()) {
    		viewNode = new StructureSectionNode(node, true); 
    	} else if (node.isTerminalNode()) {
    		viewNode = new StructureSectionNode(node, false); 
    	} else {
    		viewNode = new StructurePathNode(node);
    	}
    	
    	if (!node.isBlockNode()) {
	        for (Node subNode : node.getChildren()) {
	        	ViewNode child = buildViewNode(subNode);
	        	if (child != null)
	        		viewNode.addChild(child);
	        }
    	}
        viewNode.setLineWidth(m_lineWidth);
        viewNode.setIsSelected(selected);

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
		} else {
			m_rootNode = null;
			m_doubleClickNode = null;
			m_selectedNodes.clear();
		}
	}
}
