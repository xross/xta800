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

	public void draw(Display display, GC structureGc, GC timesGc) {
		structureGc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		structureGc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
    	structureGc.fillRectangle(m_x, m_y, m_width, m_height);
       
    	structureGc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    	structureGc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        if (isSelected()) {
        	structureGc.setLineWidth(getLineWidth() + 1);
        	structureGc.drawRectangle(m_x + 1, m_y + 1, m_width - 1, m_height - 1);
        	structureGc.setLineWidth(getLineWidth() + 1);
        } else {
        	structureGc.drawRectangle(m_x, m_y, m_width, m_height);
        }

        drawTimes(display, timesGc);
	}

	public void annotateNodes(int xOffset, int yOffset, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureBlock (%d, %d)\n", xOffset, yOffset);
        m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;
		
		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}

	public boolean isFolded() {
		return false;
	}
}
