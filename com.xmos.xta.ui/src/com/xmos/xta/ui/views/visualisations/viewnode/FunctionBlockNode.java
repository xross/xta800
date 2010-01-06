package com.xmos.xta.ui.views.visualisations.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;

public class FunctionBlockNode extends FunctionNode {
    public static final boolean DEBUG = false;

    private int m_blockHeight = TEXT_AREA_Y;
    private double m_width;

	public FunctionBlockNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
		m_width = 0;
	}
	
	public int getPixelsX(Case c) {
		Status status = new Status();
		double time = m_treeNode.getAttributes().getTimeTaken(c, status, null);
		if (status.isInfinite() || status.isUnresolved()) {
			time = 0;
		}
		return (int)time;
	}

	public int getPixelsY(Case c) {
		return m_blockHeight;
	}

	public double draw(Display display, GC gc, Case c, double x, double y) {
		m_width = getPixelsX(c) * m_canvasProperties.getScale();
        if (!isVisible((int)x, (int)y))
        	return x + m_width;

        // Get the dimensions, but ensure that all non-zero times show up
        int widthInPixels = (int)m_width;
        if (widthInPixels == 0 && m_width != 0)
        	widthInPixels = 1;

        if (c == Case.BEST) {
        	gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
        	gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
        } else {
        	gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
        	gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
        }
        gc.setAlpha(100);
    	gc.fillRectangle(offsetX((int)x), offsetY((int)y), widthInPixels, m_blockHeight);
        gc.setAlpha(255);

        if (isSelected()) {
    		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        	gc.drawRectangle(offsetX((int)x), offsetY((int)y), widthInPixels, m_blockHeight);
        }
        return x + m_width;
	}

	public boolean isVisible(int x, int y) {
		Rectangle visibleArea = m_canvasProperties.getVisibleArea();
		if ((x + m_width < visibleArea.x) ||
			(x > visibleArea.x + visibleArea.width) ||
			(y + m_blockHeight < visibleArea.y) ||
			(y > visibleArea.y + visibleArea.height)) {
			return false;
		} else {
			return true;
		}
	}

	public double getWidth(Case c) {
		Status status = new Status();
		double time = m_treeNode.getAttributes().getTimeTaken(c, status, null);
		if (status.isInfinite() || status.isUnresolved()) {
			return 0;
		} else {
			return time;
		}
	}

	public ViewNode getClickNode(int x, int y, Case c) {
		if (x < getWidth(c) && y < m_blockHeight) {
			return this;
		}
		return null;
	}
}
