package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class StructureSectionNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 4;
    private int m_borderY = 2;
    private int m_gapY = 12;
    
    private boolean m_isOptional;
    private boolean m_continues;
    
	public StructureSectionNode(Node treeNode, boolean continues) {
		super(treeNode);
		
		m_isOptional = m_treeNode.isSectionOptional();
		m_continues = continues;
	}
	
	public int getPixelsX() {
		return getChildrenX() + (2 * m_borderX);
	}

	public int getPixelsY() {
		return getChildrenY() + (2 * m_borderY);
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
			pixels += child.getPixelsY();
		}
		pixels += (getChildren().size() - 1) * m_gapY;
		
		if (m_isOptional)
			pixels += m_gapY;
		
		return pixels;
	}

	public void draw(Display display, GC gc, double scale, boolean isWorstCase) {
        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX, scale);
        m_y = scale(m_pixelOffsetY, scale);
        m_width = scale(getPixelsX(), scale);
        m_height = scale(getPixelsY(), scale);
        drawThis(display, gc);

        int midY = scale(m_pixelOffsetY + (getPixelsY() / 2), scale);
    	int childOffsetY = m_borderY;
		for (ViewNode child : getChildren()) {
			boolean childIsWorstCase = isWorstCase && (child.getTreeNode().getAttributes().getTimeTaken(Case.WORST, false) > 0);

    		int childY = child.getPixelsY();
    		int childMid = scale(m_pixelOffsetY + childOffsetY + (childY / 2), scale);

        	int childLeft = scale(child.getPixelOffsetX(), scale);
        	int childRight = scale(child.getPixelOffsetX() + child.getPixelsX(), scale);
        	setConnectionColour(display, gc, childIsWorstCase);
        	drawConnection(display, gc, m_x, midY, m_x, childMid);
        	drawConnection(display, gc, m_x, childMid, childLeft, childMid);
        	
        	if (m_continues) {
        		drawConnection(display, gc, m_x + m_width, midY, m_x + m_width, childMid);
        		drawConnection(display, gc, m_x + m_width, childMid, childRight, childMid);
        	}
            childOffsetY += childY + m_gapY;

            child.draw(display, gc, scale, childIsWorstCase);
    	}

		if (m_isOptional) {
        	setConnectionColour(display, gc, false);
			int optionalMid = scale(m_pixelOffsetY + childOffsetY - (m_gapY / 2), scale);
			drawConnection(display, gc, m_x, midY, m_x, optionalMid);
			drawConnection(display, gc, m_x, optionalMid, m_x + m_width, optionalMid);
			drawConnection(display, gc, m_x + m_width, midY, m_x + m_width, optionalMid);
		}
	}

	public void annotateXy(int xOffset, int yOffset) {
		if (DEBUG) System.out.printf("Annotate StructureSection (%d, %d)\n", xOffset, yOffset);

		m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;

    	int childOffsetY = m_borderY;
    	int childrenX = getChildrenX();
		for (ViewNode child : getChildren()) {
			int childX = child.getPixelsX();
    		child.annotateXy(xOffset + m_borderX + ((childrenX - childX) / 2), yOffset + childOffsetY);
    		childOffsetY += child.getPixelsY() + m_gapY;
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
