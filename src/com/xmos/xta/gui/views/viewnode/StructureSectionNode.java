package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;

public class StructureSectionNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 4;
    private int m_borderY = 4;
    private int m_gapLen = 16;
    
    private int m_x;
    private int m_y;
    private int m_width;
    private int m_height;
    private boolean m_isOptional;
    
	public StructureSectionNode(Node treeNode) {
		super(treeNode);
		
		m_isOptional = ((SectionNode)m_treeNode).isSectionOptional();

		m_pixelsX = 2 * m_borderX;
		m_pixelsY = 2 * m_borderY;
	}
	
	public int getPixelsX() {
		int pixels = getChildrenX() + m_pixelsX;
		if (DEBUG) System.out.printf("StructureSection X %d\n", pixels);
		return pixels;
	}

	public int getPixelsY() {
		int pixels = getChildrenY() + m_pixelsY;
		if (DEBUG) System.out.printf("StructureSection Y %d\n", pixels);
		return pixels;
	}

	public int getChildrenX() {
		int pixels = 0;
		for (ViewNode child : getChildren()) {
			if (child != null) {
				int childPixels = child.getPixelsX();
				if (childPixels > pixels)
					pixels = childPixels;
			}
		}
		return pixels;
	}

	public int getChildrenY() {
		int pixels = 0;
		for (ViewNode child : getChildren()) {
			if (child != null) {
				pixels += child.getPixelsY();
			}
		}
		pixels += (getChildren().size() - 1) * m_gapLen;
		
		if (m_isOptional)
			pixels += m_gapLen;
		
		return pixels;
	}

	public void draw(Display display, GC gc, double scale) {
        if (DEBUG) System.out.printf("Draw StructureSection %d,%d,%f\n",
        		m_pixelOffsetX, m_pixelOffsetY, scale);

        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX, scale);
        m_y = scale(m_pixelOffsetY, scale);
        m_width = scale(getPixelsX(), scale);
        m_height = scale(getPixelsY(), scale);
        
//        gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
//    	gc.fillRectangle(m_x, m_y, m_width, m_height);

        int mid = scale(m_pixelOffsetY + (getPixelsY() / 2), scale);
    	int childOffsetY = m_borderY;
		for (ViewNode child : getChildren()) {
    		child.draw(display, gc, scale);

    		int childY = child.getPixelsY();
    		int childMid = scale(m_pixelOffsetY + childOffsetY + (childY / 2), scale);

        	int childLeft = scale(child.getPixelOffsetX(), scale);
        	int childRight = scale(child.getPixelOffsetX() + child.getPixelsX(), scale);
        	drawConnection(display, gc, m_x, mid, m_x, childMid);
        	drawConnection(display, gc, m_x, childMid, childLeft, childMid);
        	drawConnection(display, gc, m_x + m_width, mid, m_x + m_width, childMid);
        	drawConnection(display, gc, m_x + m_width, childMid, childRight, childMid);

            childOffsetY += childY;
    	}

		if (m_isOptional) {
			int optionalMid = scale(m_pixelOffsetY + childOffsetY + m_gapLen / 2, scale);
			drawConnection(display, gc, m_x, mid, m_x, optionalMid);
			drawConnection(display, gc, m_x, optionalMid, m_x + m_width, optionalMid);
			drawConnection(display, gc, m_x + m_width, mid, m_x + m_width, optionalMid);
		}
	}

	public void annotateXy(int xOffset, int yOffset) {
		if (DEBUG) System.out.printf("Annotate StructureSection (%d, %d\n", xOffset, yOffset);

		m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;

    	int childOffsetY = m_borderY;
    	int childrenX = getChildrenX();
		for (ViewNode child : getChildren()) {
			int childX = child.getPixelsX();
    		child.annotateXy(xOffset + m_borderX + ((childrenX - childX) / 2), yOffset + childOffsetY);
    		childOffsetY += child.getPixelsY() + m_gapLen;
    	}
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
