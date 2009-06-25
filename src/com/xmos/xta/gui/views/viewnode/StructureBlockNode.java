package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class StructureBlockNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_minBlockX = 10;
    private int m_minBlockY = 10;

	public StructureBlockNode(Node treeNode) {
		super(treeNode);
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

	public void draw(Display display, GC gc, double scale, boolean isWorstCase) {
        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX, scale);
        m_y = scale(m_pixelOffsetY, scale);
        m_width = scale(getPixelsX(), scale);
        m_height = scale(getPixelsY(), scale);

        gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    	gc.fillRectangle(m_x, m_y, m_width, m_height);
       
        if (m_isSelected) gc.setLineWidth(m_lineWidth + 1);
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.drawRectangle(m_x, m_y, m_width, m_height);
        if (m_isSelected) gc.setLineWidth(m_lineWidth);
	}

	public void annotateXy(int xOffset, int yOffset) {
		if (DEBUG) System.out.printf("Annotate StructureBlock (%d, %d)\n", xOffset, yOffset);
        m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
		for (ViewNode child : getChildren()) {
			child.buildClickAreas(clickAreas);
    	}
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
