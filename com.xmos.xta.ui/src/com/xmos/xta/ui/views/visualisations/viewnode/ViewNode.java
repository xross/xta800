package com.xmos.xta.ui.views.visualisations.viewnode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.ConditionalNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;

public abstract class ViewNode {
	Node m_treeNode;
    private List<ViewNode> m_children;

    static final int NAME_PADDING = 5;
    static final int CORNER_ROUNDING = 7;
    public static final int TEXT_AREA_Y = 20;

    ZoomedCanvasProperties m_canvasProperties;
	
    public ViewNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
    	m_treeNode = treeNode;

		m_children = new ArrayList<ViewNode>();
		m_canvasProperties = canvasProperties;
    }
    
    public abstract int getPixelOffsetX();
    public abstract int getPixelOffsetY();
    public abstract int getPixelsX();
    public abstract int getPixelsY();
    public abstract int getPixelsY(Case c);
    public abstract int getChildrenX();
    public abstract int getChildrenY();
    public abstract void draw(Display display, GC gc);
	public abstract double draw(Display display, GC gc, Case c, double x, double y);
    public abstract void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase);
	public abstract void updateScaledXy();
	public abstract boolean contains(int x, int y);
	public abstract ViewNode getClickNode(int x, int y);
    public abstract boolean isBestCase();
    public abstract boolean isWorstCase();

    public Node getTreeNode() {
    	return m_treeNode;
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

    public boolean isInfinite() {
		for (ViewNode child : getChildren()) {
			if (child.isInfinite()) {
				return true;
			}
    	}
		return false;
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
		ConditionalNode condNode = (ConditionalNode)m_treeNode;
		Node optionChild = condNode.getOption(c, null);

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

	public boolean isFolded() {
		return m_treeNode.isFolded();
	}
}
