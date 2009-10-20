package com.xmos.xta.ui.views.visualisations.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;

public class FunctionFunctionNode extends FunctionNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 2;
    private int m_borderY = 2;
    private int m_textAreaX = 0;
    
	public FunctionFunctionNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
		m_textAreaX = canvasProperties.getAverageCharWidth() * treeNode.getFunctionName().length() + NAME_PADDING;
	}

	public int getPixelsY() {
		// The height of both best and worst case
		return getChildrenY() + (TEXT_AREA_Y * 2);
	}

	public int getPixelsY(Case c) {
		int pixels = 0;
		for (ViewNode child : getChildren()) {
			int childPixels = child.getPixelsY(c);
			if (childPixels > pixels)
				pixels = childPixels;
		}
		return pixels + TEXT_AREA_Y;
    }

	public double draw(Display display, GC gc, Case c, double x, double y) {
		int startX = (int)x;
		for (ViewNode child : getChildren()) {
			x = child.draw(display, gc, c, x, y + TEXT_AREA_Y);
    	}

    	// Draw the function name
		if (((int)x - startX) > m_textAreaX) {
	        int textBase = (int)y + m_borderY + TEXT_AREA_Y;
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

			gc.drawString(m_treeNode.getFunctionName(), offsetX(startX + m_borderX), scaleY(textBase));
		}
		return x;
	}
}
