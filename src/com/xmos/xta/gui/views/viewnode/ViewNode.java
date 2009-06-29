package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public abstract class ViewNode {
	private static final int MIN_PIXELS = 2;

	protected Node m_treeNode;
    private List<ViewNode> m_children;
    
    protected int m_pixelOffsetX;
    protected int m_pixelOffsetY;

    protected int m_cornerRounding = 7;
    protected int m_x;
    protected int m_y;
    protected int m_width;
    protected int m_height;

    private boolean m_isVisible;
    private ZoomedCanvasProperties m_canvasProperties;
	
    public ViewNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
    	m_treeNode = treeNode;

	    m_pixelOffsetX = 0;
	    m_pixelOffsetY = 0;

		m_isVisible = true;
		
		m_children = new ArrayList<ViewNode>();
		m_canvasProperties = canvasProperties;
    }
    
    public abstract int getPixelsX();
    public abstract int getPixelsY();
    public abstract int getChildrenX();
    public abstract int getChildrenY();
    public abstract void draw(Display display, GC gc, boolean isWorstCase, boolean isBestCase);
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

    public int scaleX(int value) {
    	return scale(value) - m_canvasProperties.getVisibleArea().x;
    }

    public int scaleY(int value) {
    	return scale(value) - m_canvasProperties.getVisibleArea().y;
    }

    public boolean isSelected() {
		return m_canvasProperties.isSelected(this);
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
			boolean childIsWorstCase, boolean childIsBestCase) {
		gc.setLineWidth(getLineWidth() + 1);
		
		if (childIsBestCase) {
			gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
			gc.drawLine(x1, y1, x2, y2);
			
			if (childIsWorstCase) {
				gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
				gc.setLineStyle(SWT.LINE_DASH);
				gc.drawLine(x1, y1, x2, y2);
				gc.setLineStyle(SWT.LINE_SOLID);
			}

		} else if (childIsWorstCase) {
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
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
	    	gc.setAlpha(100);
	    	gc.fillRoundRectangle(m_x, m_y, m_width, m_height, m_cornerRounding, m_cornerRounding);
	    	gc.setAlpha(255);
	    }
	}

	protected void setXy() {
		int pixelsX = getPixelsX();
		int pixelsY = getPixelsY();
	    Rectangle visibleArea = m_canvasProperties.getVisibleArea();
		
        m_x = scaleX(m_pixelOffsetX);
        m_y = scaleY(m_pixelOffsetY);
        m_width = scale(pixelsX);
        m_height = scale(pixelsY);
        
        int offsetX = scale(m_pixelOffsetX);
        int offsetY = scale(m_pixelOffsetY);
		if (m_height < MIN_PIXELS || m_width < MIN_PIXELS ||
			(offsetX + m_width < visibleArea.x) ||
			(offsetX > visibleArea.x + visibleArea.width) ||
			(offsetY + m_height < visibleArea.y) ||
			(offsetY > visibleArea.y + visibleArea.height)) {
	        m_isVisible = false;
		} else {
			m_isVisible = true;
		}
	}

	protected boolean isVisible() {
		return m_isVisible;
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
		int thisX = scaleX(m_pixelOffsetX);
		int thisY = scaleY(m_pixelOffsetY);
		int width = scale(getPixelsX());
		int height = scale(getPixelsY());
		if ((x >= thisX) && (x <= (thisX + width))) {
			if ((y >= thisY) && (y <= (thisY + height))) {
				return true;
			}
		}
		return false;
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
		if (!isVisible()) {
			return;
		}
		if (!isFolded()) {
			for (ViewNode child : getChildren()) {
				child.buildClickAreas(clickAreas);
	    	}
		}
    	clickAreas.add(this);
	}
}
