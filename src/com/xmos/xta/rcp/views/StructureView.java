package com.xmos.xta.rcp.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.views.ViewNode.StructureLoopNode;
import com.xmos.xta.rcp.views.ViewNode.StructureNode;
import com.xmos.xta.rcp.views.ViewNode.StructurePathNode;
import com.xmos.xta.rcp.views.ViewNode.StructureRouteNode;

public class StructureView extends ZoomedCanvasView {

	public static final boolean DEBUG = false;
    public static final int MAX_NUM_RENDER_NODES = 2000;
    public static final String ID = "com.xmos.xta.rcp.views.Structure";

    private int m_numTreeNodes;

	public StructureView() {
		super();
		if (DEBUG) System.out.printf("Structure()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	    
		m_scale = 1.0;
		m_scaleY = true;
		m_pixelsX = 0;
		m_pixelsY = 0;
		m_borderX = 10;
		m_borderY = 10;
		m_lineWidth = 1;
		m_numTreeNodes = 0;
	}

    protected void annotateTree(boolean isSelctionChange) {
		if (m_selectedNode != null) {
    		if (m_selectedNode.isBlockNode()) {
    			m_selectedNode = m_selectedNode.getParentNode();
    		}
    	}

		m_numTreeNodes = 0;
		annotateTree(m_rootNode, 1, 1);
		
		m_clickAreas.clear();
		if (m_rootNode != null) {
			m_rootNode.getStructureNode().annotateXy(m_borderX, m_borderY, m_clickAreas);
			updatePixelsXy();
		}
    }
    
    protected void updatePixelsXy() {
    	if ((m_rootNode == null) || (m_numTreeNodes > MAX_NUM_RENDER_NODES)) {
			m_pixelsX = 0;
			m_pixelsY = 0;
		} else {
			m_pixelsX = (m_borderX  * 2) + m_rootNode.getStructureNode().getPixelsX();
			m_pixelsY = (m_borderY * 2) + m_rootNode.getStructureNode().getPixelsY();
		}
    }
    
    protected void drawCanvas(GC gc) {
    	gc.setAntialias(SWT.ON);
    	gc.setTextAntialias(SWT.ON);
    	
		Rectangle clientArea = m_canvas.getClientArea();
		m_canvas.setSize(clientArea.width, clientArea.height);
    	gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, clientArea.width, clientArea.height);

        if (m_numTreeNodes > MAX_NUM_RENDER_NODES) {
	        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
	        gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
	        String error = "Code structure too complicated: select a smaller region";

	        double functionNameWidth = gc.getFontMetrics().getAverageCharWidth() * error.length();
	        double functionNameHeight = gc.getFontMetrics().getHeight();
	        
	        gc.drawString(error, (int)((clientArea.width - functionNameWidth)/2),
	        		(int)((clientArea.height - functionNameHeight)/2));
	    } else {
	        if (m_rootNode == null)
	        	return;
	        
			gc.setLineWidth(m_lineWidth);
			m_rootNode.getStructureNode().draw(m_display, gc, m_scale);
	    }
    }
    
	private boolean annotateTree(Node node, int numIterations, double totalTime) {
		if (node == null)
			return false;
		
    	double weight = (numIterations * node.getTimeTakenThis()) / totalTime;
    	boolean selected = (node == m_selectedNode);

    	StructureNode structureNode = null;
    	if (node.isRouteNode())
    		structureNode = new StructureRouteNode(node, weight, m_lineWidth);
    	else if (node.isPathNode())
    		structureNode = new StructurePathNode(node, weight, m_lineWidth);
    	else if (node.isLoopNode())
    		structureNode = new StructureLoopNode(node, weight, m_lineWidth);
    	else
    		assert(false);
    	node.setStructureNode(structureNode);
    	m_numTreeNodes++;
    	
        for (Node subNode : node.getNodes()) {
    		if (subNode.isPathNode() || subNode.isLoopNode()) {
            	if (node.isRouteNode())
            		totalTime = subNode.getTimeTaken(); // Re-compute total time for sub tree

    			int subNumIterations = numIterations;
    			if (node.isLoopNode())
    				subNumIterations = subNode.getNumIterations() * numIterations;
    			
    			selected |= annotateTree(subNode, subNumIterations, totalTime);
    		}
        }

        structureNode.setIsSelected(selected);
   		return selected;
	}

	protected void initialiseTree() {
		m_routeInfo = XtaDisplayer.getInstance().getRouteInfo();
		if (m_routeInfo != null) {
			m_rootNode = m_routeInfo.getRouteNode();
			m_selectedNode = m_routeInfo.getSelectedNode();
		} else {
			m_rootNode = null;
			m_selectedNode = null;
		}
	}
}