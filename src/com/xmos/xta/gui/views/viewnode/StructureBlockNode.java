package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.structure.ZoomedCanvasProperties;

public class StructureBlockNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_minBlockX = 2;
    private int m_minBlockY = 10;

	public StructureBlockNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}

	public int getPixelsX() {
		return m_minBlockX + m_treeNode.getChildren().size();
	}

	public int getPixelsY() {
		return m_minBlockY;
	}

	public int getChildrenX() {
		return 0;
	}

	public int getChildrenY() {
		return 0;
	}

	public void draw(Display display, GC gc, boolean isWorstCase, boolean isBestCase) {
		if (!isVisible()) {
			return;
		}

		gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    	gc.fillRectangle(m_x, m_y, m_width, m_height);
       
        if (isSelected()) gc.setLineWidth(getLineWidth() + 1);
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.drawRectangle(m_x, m_y, m_width, m_height);
        if (isSelected()) gc.setLineWidth(getLineWidth());
	}

	public void drawTimes(Display display, GC gc, boolean isWorstCase, boolean isBestCase) {
		if (!isVisibleX()) {
			return;
		}
		
		if (!setTimesY())
			return;

		gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
    	gc.fillRectangle(m_x, 0, 5, m_worstHeight);
		gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
    	gc.fillRectangle(m_x + 5, 0, 5, m_bestHeight);

    	if (isSelected()) {
	        gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.drawRectangle(m_x, 0, 5, m_worstHeight);
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.drawRectangle(m_x + 5, 0, 5, m_bestHeight);
    	}
    	
		for (ViewNode child : getChildren()) {
    		child.drawTimes(display, gc, isWorstCase, isBestCase);
    	}
	}

	public void annotateXy(int xOffset, int yOffset) {
		if (DEBUG) System.out.printf("Annotate StructureBlock (%d, %d)\n", xOffset, yOffset);
        m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;
	}

	public boolean isFolded() {
		return false;
	}
}
