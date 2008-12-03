package com.xmos.xta.rcp.views;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.views.ViewNode.StructureNode;

public class StructureView extends ZoomedCanvasView {

	public static final boolean DEBUG = false;
    public static final int MAX_NUM_RENDER_NODES = 2000;
    public static final String ID = "com.xmos.xta.rcp.views.Structure";

    private boolean m_weightIsCumulative;
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
		m_weightIsCumulative = false;
	}

    protected void buildClickAreas() {
    	m_clickAreas.clear();
    	if (m_rootNode != null) {
    		m_rootNode.getStructureNode().buildClickAreas(m_clickAreas);
    	}
    }

    protected void annotateTree(boolean isSelctionChange) {
		if (m_selectedNode != null) {
    		if (m_selectedNode.isBlockNode()) {
    			m_selectedNode = m_selectedNode.getParentNode();
    		}
    	}

		m_numTreeNodes = 0;
		annotateTree(m_doubleClickNode,
				getNumIterations(m_doubleClickNode),
				getTotalWeight(m_doubleClickNode));
		
		// Build up a new list of click areas and then swap to ensure the old ones are available
		// for double clicks
		if (m_doubleClickNode != null) {
			m_doubleClickNode.getStructureNode().annotateXy(m_borderX, m_borderY);
			updatePixelsXy();
		}
    }

    private int getNumIterations(Node start) {
    	if ((start == null) || start.isRouteNode()) 
    		return 1;
    	
		return (start.getNumIterations() * getNumIterations(start.getParentNode()));
	}

    private double getTotalWeight(Node start) {
    	if ((start == null) || start.isRouteNode()) { 
    		return 1;
    	} else {
    		Node parent = start.getParentNode();
    		if (parent.isRouteNode()) {
    			return start.getTimeTaken();
    		} else {
    			return getTotalWeight(parent);
    		}
    	}
	}

    protected void updatePixelsXy() {
    	if ((m_doubleClickNode == null) || (m_numTreeNodes > MAX_NUM_RENDER_NODES)) {
			m_pixelsX = 0;
			m_pixelsY = 0;
		} else {
			m_pixelsX = (m_borderX  * 2) + m_doubleClickNode.getStructureNode().getPixelsX();
			m_pixelsY = (m_borderY * 2) + m_doubleClickNode.getStructureNode().getPixelsY();
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
	        if (m_doubleClickNode == null)
	        	return;
	        
			gc.setLineWidth(m_lineWidth);
			m_doubleClickNode.getStructureNode().draw(m_display, gc, m_scale);
	    }
    }
    
	private boolean annotateTree(Node node, int numIterations, double totalTime) {
		if (node == null)
			return false;
    	m_numTreeNodes++;
		
    	double weight = 0;
    	if (totalTime > 0) {
    		weight = (numIterations * node.getTimeTakenThis()) / totalTime;
    	}

    	boolean selected = (node == m_selectedNode);

    	StructureNode structureNode = node.getStructureNode();

    	double childrenWeight = 0;
        for (Node subNode : node.getNodes()) {
    		if (subNode.isPathNode() || subNode.isLoopNode()) {
            	if (node.isRouteNode())
            		totalTime = subNode.getTimeTaken(); // Re-compute total time for sub tree

    			int subNumIterations = numIterations;
    			if (node.isLoopNode())
    				subNumIterations = subNode.getNumIterations() * numIterations;
    			
    			selected |= annotateTree(subNode, subNumIterations, totalTime);
    			
    			StructureNode subStructureNode = subNode.getStructureNode();
    			if (subStructureNode != null) {
    				childrenWeight += subStructureNode.getWeight();
    			}
    		}
        }

        if (m_weightIsCumulative) {
        	structureNode.setWeight(weight + childrenWeight);
        } else {
        	structureNode.setWeight(weight);
        }
        
    	structureNode.setLineWidth(m_lineWidth);
        structureNode.setIsSelected(selected);
   		return selected;
	}

	protected void initialiseTree() {
		m_routeInfo = XtaDisplayer.getInstance().getRouteInfo();
		if (m_routeInfo != null) {
			m_rootNode = m_routeInfo.getRouteNode();
			m_doubleClickNode = m_rootNode;
			m_selectedNode = m_routeInfo.getSelectedNode();
			setSelection(new StructuredSelection(m_selectedNode));
		} else {
			m_rootNode = null;
			m_doubleClickNode = null;
			m_selectedNode = null;
		}
	}

	public void setWeightCumulative(boolean cumulative) {
		m_weightIsCumulative = cumulative;
		refreshView();
		annotateTree(false);
	}
}