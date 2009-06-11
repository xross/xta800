package com.xmos.xta.gui.views.viewnode;
/*
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class FunctionBlockNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_height = 20;

    private int m_x;
    private int m_y;
    private int m_width;

	public FunctionBlockNode(Node treeNode) {
		super(treeNode);
	}
	
	public void setNumIterations(long numIterations) {
		m_numIterations = numIterations;
		m_pixelsX = (int) (m_treeNode.getNumCycles(Case.WORST) * numIterations);
		m_pixelsY = m_height;
	}

	public int getPixelsX() {
		if (m_numIterations == 0)
			return 0;
		
		int pixels = getChildrenX();
		if (m_pixelsX > pixels)
			pixels = m_pixelsX;
		if (DEBUG) System.out.printf("FunctionBlock X %d\n", pixels);
		return pixels;
	}

	public int getPixelsY() {
		if (m_numIterations == 0)
			return 0;
		
		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("FunctionBlock Y %d\n", pixels);
		return pixels;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			ViewNode child = treeNode.getFunctionNode();
			if (child != null) {
				pixels += child.getPixelsX();
			}
		}
		return pixels;
	}

	public int getChildrenY() {
		int pixels = 0;
		for (Node treeNode : m_treeNode.getChildren()) {
			ViewNode child = treeNode.getFunctionNode();
			if (child != null) {
				int childPixels = child.getPixelsY();
				if (childPixels > pixels)
					pixels = childPixels;
			}
		}
		return pixels;
	}

	public void draw(Display display, GC gc, double scale) {
        if (m_numIterations == 0)
        	return;

        if (DEBUG) System.out.printf("Draw FunctionBlock %d,%d,%f\n",
        		m_pixelOffsetX, m_pixelOffsetY, scale);

        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX, scale);
        m_y = m_pixelOffsetY;
        m_width = scale(m_pixelsX, scale);
        m_height = m_pixelsY;

        if (m_isSelected) {
        	gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
        } else { 
        	gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
        }
    	gc.fillRectangle(m_x, m_y, m_width, m_height);

    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        int x = m_x;
        int y = m_y;
        if (m_isSelected) {
        	gc.setLineWidth(m_lineWidth + 1);
        	x += 1;
        	y += 1;
        }
    	gc.drawRectangle(x, y, m_width, m_height);
	}

	public void annotateXy(int xOffset, int yOffset) {
        m_pixelOffsetX = xOffset;
        m_pixelOffsetY = yOffset;
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
        clickAreas.add(this);
	}

	public boolean contains(int x, int y) {
		if ((x >= m_x) && (x <= (m_x + m_width))) {
			if ((y >= m_y) && (y <= (m_y + m_height))) {
				return true;
			}
		}
		return false;
	}
}
*/