package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.gui.views.structure.ZoomedCanvasProperties;

public abstract class ViewNode {
	protected Node m_treeNode;
    private List<ViewNode> m_children;

    private static final int TIMES_WIDTH = 6;
    private static final int CORNER_ROUNDING = 7;

    protected int m_pixelOffsetX;
    protected int m_pixelOffsetY;

    protected int m_x;
    protected int m_y;
    protected int m_width;
    protected int m_height;
    
    protected int m_bestHeight;
    protected int m_worstHeight;

    private ZoomedCanvasProperties m_canvasProperties;
	
    public ViewNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
    	m_treeNode = treeNode;

	    m_pixelOffsetX = 0;
	    m_pixelOffsetY = 0;

		m_children = new ArrayList<ViewNode>();
		m_canvasProperties = canvasProperties;
    }
    
    public abstract int getPixelsX();
    public abstract int getPixelsY();
    public abstract int getChildrenX();
    public abstract int getChildrenY();
    public abstract void draw(Display display, GC structureGc, GC timesGc);
    public abstract void annotateXy(int xOffset, int yOffset);

    public Node getTreeNode() {
    	return m_treeNode;
    }

    public int getPixelOffsetX() {
    	return m_pixelOffsetX;
    }

    public int getPixelOffsetY() {
    	return m_pixelOffsetY;
    }

    public int scale(int value) {
    	return (int)((double)value * m_canvasProperties.getScale());
    }

    public boolean isSelected() {
		return m_canvasProperties.isSelected(this);
	}

    public boolean isBestCase() {
		return (m_treeNode.getAttributes().getTimeTaken(Case.BEST, false) > 0);
	}

    public boolean isWorstCase() {
		return (m_treeNode.getAttributes().getTimeTaken(Case.WORST, false) > 0);
	}

	public String getToolTipText() {
		return m_treeNode.getAttributes().getTimingDetails();
	}

	public void addChild(ViewNode child) {
		m_children.add(child);
	}
	
	public List<ViewNode> getChildren() {
		return m_children;
	}

	public void drawConnection(Display display, GC gc, int x1, int y1, int x2, int y2,
			boolean isWorstCase, boolean isBestCase) {
		gc.setLineWidth(getLineWidth() + 1);
		
		if (isBestCase) {
			gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
			gc.drawLine(x1, y1, x2, y2);
			
			if (isWorstCase) {
				gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
				gc.setLineStyle(SWT.LINE_DASH);
				gc.drawLine(x1, y1, x2, y2);
				gc.setLineStyle(SWT.LINE_SOLID);
			}

		} else if (isWorstCase) {
			gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
			gc.drawLine(x1, y1, x2, y2);

		} else {
			gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
			gc.drawLine(x1, y1, x2, y2);
			
		}
		gc.setLineWidth(getLineWidth());
	}

	public void drawThis(Display display, GC gc) {
	    if (isSelected()) {
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
	    	gc.setAlpha(100);
	    	gc.fillRoundRectangle(m_x, m_y, m_width, m_height, CORNER_ROUNDING, CORNER_ROUNDING);
	    	gc.setAlpha(255);
	    } else {
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
	    	gc.fillRectangle(m_x, m_y, m_width, m_height);
	    }
	}

	public void updateScaledXy() {
		int pixelsX = getPixelsX();
		int pixelsY = getPixelsY();
		
        m_x = scale(m_pixelOffsetX);
        m_y = scale(m_pixelOffsetY);
        m_width = scale(pixelsX);
        m_height = scale(pixelsY);

		for (ViewNode child : getChildren()) {
    		child.updateScaledXy();
		}
	}

	protected boolean setTimesY() {
	    int timesHeight = m_canvasProperties.getTimesHeight();
        m_bestHeight = (int)((double)timesHeight * 
        	getTreeNode().getAttributes().getTimeTaken(Case.BEST, false) / m_canvasProperties.getTotalBest());

        m_worstHeight = (int)((double)timesHeight * 
    		getTreeNode().getAttributes().getTimeTaken(Case.WORST, false) / m_canvasProperties.getTotalWorst());

		if (m_bestHeight < 1 && m_worstHeight < 1) {
	        return false;
		}
		return true;
	}

	public void drawTimes(Display display, GC timesGc) {
		if (!setTimesY())
			return;
		
		timesGc.setBackground(display.getSystemColor(SWT.COLOR_RED));
    	timesGc.fillRectangle(m_x, 0, TIMES_WIDTH, m_worstHeight);
		timesGc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
    	timesGc.fillRectangle(m_x + TIMES_WIDTH, 0, TIMES_WIDTH, m_bestHeight);

    	if (isSelected()) {
	        timesGc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	    	timesGc.drawRectangle(m_x, 0, TIMES_WIDTH - 1, m_worstHeight - 1);
	    	timesGc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	    	timesGc.drawRectangle(m_x + TIMES_WIDTH, 0, TIMES_WIDTH - 1, m_bestHeight - 1);
    	}
	}
	
	public void redraw(Display display, GC structureGc, GC timesGc, List<Node> nodes) {
		if (nodes.contains(m_treeNode)) {
			draw(display, structureGc, timesGc);
		} else {
			for (ViewNode child : getChildren()) {
				child.redraw(display, structureGc, timesGc, nodes);
			}
		}
	}
	
	protected int getLineWidth() {
		return m_canvasProperties.getLineWidth();
	}

	public void setFolded(boolean folded, boolean applyToChildren) {
		m_treeNode.setFolded(folded, applyToChildren);
	}

	public boolean isFolded() {
		return m_treeNode.isFolded();
	}

	public boolean contains(int x, int y) {
		int thisX = scale(m_pixelOffsetX);
		int thisY = scale(m_pixelOffsetY);
		int width = scale(getPixelsX());
		int height = scale(getPixelsY());
		if ((x >= thisX) && (x <= (thisX + width))) {
			if ((y >= thisY) && (y <= (thisY + height))) {
				return true;
			}
		}
		return false;
	}

	public ViewNode getClickNode(int x, int y) {
		if (this.contains(x, y)) {
			for (ViewNode child : getChildren()) {
				ViewNode clickNode = child.getClickNode(x, y);
				if (clickNode != null)
					return clickNode;
	    	}
			return this;
		} else {
			return null;
		}
	}
}
