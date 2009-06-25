package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class StructureLoopNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 8;
    private int m_borderY = 4;
    private int m_gapX = 8;
    private int m_gapY = 4;
    
    private ViewNode m_headerNode = null;
    
	public StructureLoopNode(Node treeNode) {
		super(treeNode);
	}
	
	public int getPixelsX() {
		return getChildrenX() + m_gapX + (2 * m_borderX);
	}

	public int getPixelsY() {
		return getChildrenY() + (2 * m_borderY);
	}

	public int getChildrenX() {
		int pixels = m_headerNode.getPixelsX() + m_gapX;

		int childrenX = 0;
		for (ViewNode child : getChildren()) {
			int childX = child.getPixelsX();
			if (childX > childrenX)
				childrenX = childX;
		}
		return pixels + childrenX;
	}

	public int getChildrenY() {
		int pixels = m_headerNode.getPixelsY() + m_gapY;
		for (ViewNode child : getChildren()) {
			pixels += child.getPixelsY() + m_gapY;
		}
		return pixels;
	}

	public void draw(Display display, GC gc, double scale, boolean isWorstCase) {
        gc.setLineWidth(m_lineWidth);
        m_x = scale(m_pixelOffsetX, scale);
        m_y = scale(m_pixelOffsetY, scale);
        m_width = scale(getPixelsX(), scale);
        m_height = scale(getPixelsY(), scale);
        
        drawThis(display, gc);

        drawHeader(display, gc, scale, isWorstCase);
        
        int headerY = scale(m_headerNode.getPixelOffsetY() + (m_headerNode.getPixelsY() / 2), scale);
        int headerOutX = scale(m_headerNode.getPixelOffsetX() + m_headerNode.getPixelsX() + (m_gapX / 2), scale);
        int headerInX = scale(m_headerNode.getPixelOffsetX() - (m_gapX / 2), scale);
        int loopReturnX = scale(m_pixelOffsetX + getPixelsX() - (m_gapX / 2), scale);
        int loopReturnY = scale(m_pixelOffsetY + getPixelsY() - (m_gapY / 2), scale);

        boolean loopWorstCase = false; 
        if (getChildren().size() > 0) {
	        int childMinY = 0;
	        int childMaxY = 0;
	    	for (ViewNode child : getChildren()) {
				boolean childIsWorstCase = isWorstCase && (child.getTreeNode().getAttributes().getTimeTaken(Case.WORST, false) > 0);
	            int childY = scale(child.getPixelOffsetY() + (child.getPixelsY() / 2), scale);
	            int childLeft = scale(child.getPixelOffsetX(), scale);
	            int childRight = scale(child.getPixelOffsetX() + child.getPixelsX(), scale);

	        	setConnectionColour(display, gc, childIsWorstCase);
	        	drawConnection(display, gc, headerOutX, childY, childLeft, childY);
	    		drawConnection(display, gc, childRight, childY, loopReturnX, childY);
	    		
	    		if (childMinY == 0) {
	    			childMinY = childY;
	    		}
	    		childMaxY = childY;
	    		
	    		child.draw(display, gc, scale, childIsWorstCase);
	    		loopWorstCase |= childIsWorstCase;
	    	}
	    	
	    	setConnectionColour(display, gc, loopWorstCase);
			drawConnection(display, gc, headerOutX, headerY, headerOutX, childMaxY);
			drawConnection(display, gc, loopReturnX, childMinY, loopReturnX, loopReturnY);
			drawConnection(display, gc, headerInX, loopReturnY, loopReturnX, loopReturnY);
        } else {
        	loopWorstCase = m_headerNode.getTreeNode().getAttributes().getNumIterations(Case.WORST) > 0;
        	setConnectionColour(display, gc, loopWorstCase);
			drawConnection(display, gc, headerOutX, headerY, headerOutX, loopReturnY);
			drawConnection(display, gc, headerInX, loopReturnY, headerOutX, loopReturnY);
        }
    	setConnectionColour(display, gc, loopWorstCase);
		drawConnection(display, gc, headerInX, headerY, headerInX, loopReturnY);
		
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		int[] outTriangle = {
				headerOutX, headerY + (2 * m_borderY),
				headerOutX + (m_borderX / 2), headerY + m_borderY,
				headerOutX - (m_borderX / 2), headerY + m_borderY,};
		gc.fillPolygon(outTriangle);

		int[] returnTriangle = {
				headerInX, headerY + m_borderY,
				headerInX + (m_borderX / 2), headerY + (2 * m_borderY),
				headerInX - (m_borderX / 2), headerY + (2 * m_borderY),};
		gc.fillPolygon(returnTriangle);
	}

	private void drawHeader(Display display, GC gc, double scale, boolean isWorstCase) {
        m_headerNode.draw(display, gc, scale, isWorstCase);

        // Wire through the header
        int midY = scale(m_pixelOffsetY + (getPixelsY() / 2), scale);
        int right = m_x + m_width;
        int headerY = scale(m_headerNode.getPixelOffsetY() + (m_headerNode.getPixelsY() / 2), scale);
        int headerRight = scale(m_headerNode.getPixelOffsetX() + m_headerNode.getPixelsX(), scale);

    	setConnectionColour(display, gc, isWorstCase);
    	drawConnection(display, gc, m_x, midY, m_x, headerY);
		drawConnection(display, gc, m_x, headerY, scale(m_headerNode.getPixelOffsetX(), scale), headerY);
		drawConnection(display, gc, headerRight, headerY, right, headerY);
		drawConnection(display, gc, right, midY, right, headerY);
	}

	public void annotateXy(int xOffset, int yOffset) {
		if (DEBUG) System.out.printf("Annotate StructureLoop (%d, %d\n", xOffset, yOffset);

		m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;
		
		m_headerNode.annotateXy(xOffset + m_borderX + m_gapX, yOffset + m_borderY);

        int childOffsetX = m_borderX + m_headerNode.getPixelsX() + (2 * m_gapX);
        int childOffsetY = m_borderY + m_headerNode.getPixelsY() + m_gapY;
		for (ViewNode child : getChildren()) {
    		child.annotateXy(xOffset + childOffsetX, yOffset + childOffsetY);
    		childOffsetY += child.getPixelsY() + m_gapY;
    	}
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
		m_headerNode.buildClickAreas(clickAreas);
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

	public void addChild(ViewNode child) {
		if (m_headerNode == null) {
			m_headerNode = child;
		} else {
			super.addChild(child);
		}
	}
}
