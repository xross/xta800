package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class FunctionBlockNode extends FunctionNode {
    public static final boolean DEBUG = false;

    private int m_blockHeight = TEXT_AREA_Y;

	public FunctionBlockNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}
	
	public int getPixelsX() {
		return (int)m_treeNode.getAttributes().getTimeTaken(Case.WORST, false);
	}

	public int getPixelsY() {
		return m_blockHeight;
	}

	public int getPixelsY(Case c) {
		return m_blockHeight;
	}

	public int getChildrenX() {
		return 0;
	}

	public int getChildrenY() {
		return 0;
	}

	public double draw(Display display, GC gc, Case c, double x, double y) {
        if (!isVisible((int)x, (int)y))
        	return x + m_width;

        int widthInPixels = ((int)m_width < 1) ? 1 : (int)m_width;
        int heightInPixels = ((int)m_height < 1) ? 1 : (int)m_height;

        if (c == Case.BEST) {
        	gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
        	gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
        } else {
        	gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
        	gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
        }
        gc.setAlpha(100);
    	gc.fillRectangle(offsetX((int)x), offsetY((int)y), widthInPixels, heightInPixels);
        gc.setAlpha(255);

        if (isSelected()) {
    		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        	gc.drawRectangle(offsetX((int)x), offsetY((int)y), widthInPixels, heightInPixels);
        }
        return x + m_width;
	}

	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate FunctionBlock (%d, %d)\n", offsetX, offsetY);
	}
}
