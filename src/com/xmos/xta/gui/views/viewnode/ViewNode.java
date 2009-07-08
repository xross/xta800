package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public abstract class ViewNode {
	Node m_treeNode;
    private List<ViewNode> m_children;

    static final int NAME_PADDING = 5;
    static final int CORNER_ROUNDING = 7;
    public static final int TEXT_AREA_Y = 20;

    int m_pixelOffsetX;
    int m_pixelOffsetY;

    int m_renderX;
    int m_renderY;
    double m_width;
    double m_height;
    
    boolean m_isBestCase;
    boolean m_isWorstCase;

    private ZoomedCanvasProperties m_canvasProperties;
	
    public ViewNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
    	m_treeNode = treeNode;

	    m_pixelOffsetX = 0;
	    m_pixelOffsetY = 0;
	    
	    m_isBestCase = false;
	    m_isWorstCase = false;

		m_children = new ArrayList<ViewNode>();
		m_canvasProperties = canvasProperties;
    }
    
    public abstract int getPixelsX();
    public abstract int getPixelsY();
    public abstract int getPixelsY(Case c);
    public abstract int getChildrenX();
    public abstract int getChildrenY();
    public abstract void draw(Display display, GC gc);
	public abstract double draw(Display display, GC gc, Case c, double x, double y);
    public abstract void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase);

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
    	if (m_canvasProperties.scaleY()) {
    		return scale(value) - m_canvasProperties.getVisibleArea().y;
    	} else {
    		return value - m_canvasProperties.getVisibleArea().y;
    	}
    }

    public int offsetX(int value) {
    	return value - m_canvasProperties.getVisibleArea().x;
    }

    public int offsetY(int value) {
		return value - m_canvasProperties.getVisibleArea().y;
    }

    public boolean isSelected() {
		return m_canvasProperties.isSelected(this);
	}

    public boolean isBestCase() {
		return m_isBestCase;
	}

    public boolean isWorstCase() {
		return m_isWorstCase;
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
			boolean isBestCase, boolean isWorstCase) {
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
	    	gc.fillRoundRectangle(m_renderX, m_renderY, (int)m_width, (int)m_height, CORNER_ROUNDING, CORNER_ROUNDING);
	    	gc.setAlpha(255);
	    } else {
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
	    	gc.fillRectangle(m_renderX, m_renderY, (int)m_width, (int)m_height);
	    }
	}

	public void updateScaledXy() {
		int pixelsX = getPixelsX();
		int pixelsY = getPixelsY();
		
        m_renderX = scaleX(m_pixelOffsetX);
        m_renderY = scaleY(m_pixelOffsetY);
        m_width = (double)pixelsX * m_canvasProperties.getScale();
        
        if (m_canvasProperties.scaleY()) {
        	m_height = (double)pixelsY * m_canvasProperties.getScale();
        } else {
        	m_height = pixelsY;
        }

		for (ViewNode child : getChildren()) {
    		child.updateScaledXy();
		}
	}

	public void redraw(Display display, GC gc, List<Node> nodes) {
		if (nodes.contains(m_treeNode)) {
			draw(display, gc);
		} else {
			for (ViewNode child : getChildren()) {
				child.redraw(display, gc, nodes);
			}
		}
	}
	
	protected int getLineWidth() {
		return m_canvasProperties.getLineWidth();
	}

	protected ViewNode getChild(Case c) {
		SectionNode sectionNode = (SectionNode)m_treeNode;
		Node optionChild = sectionNode.getOption(c, false);

		if (optionChild != null) {
			for (ViewNode child : getChildren()) {
				if (child.getTreeNode() == optionChild) {
					return child;
				}
			}
		}
		return null;
	}

	public void setFolded(boolean folded, boolean applyToChildren) {
		m_treeNode.setFolded(folded, applyToChildren);
	}

	public boolean isVisible() {
		Rectangle visibleArea = m_canvasProperties.getVisibleArea();
		int x = scale(m_pixelOffsetX);
		int y = scale(m_pixelOffsetY);
		if (m_width < 1 || m_height < 1 ||
			(x + m_width < visibleArea.x) ||
			(x > visibleArea.x + visibleArea.width) ||
			(y + m_height < visibleArea.y) ||
			(y > visibleArea.y + visibleArea.height)) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isVisible(int x, int y) {
		Rectangle visibleArea = m_canvasProperties.getVisibleArea();
		if ((x + m_width < visibleArea.x) ||
			(x > visibleArea.x + visibleArea.width) ||
			(y + m_height < visibleArea.y) ||
			(y > visibleArea.y + visibleArea.height)) {
			return false;
		} else {
			return true;
		}
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
