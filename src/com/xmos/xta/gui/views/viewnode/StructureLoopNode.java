package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.gui.views.structure.ZoomedCanvasProperties;

public class StructureLoopNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 8;
    private int m_borderY = 4;
    private int m_gapX = 8;
    private int m_gapY = 4;
    
    private ViewNode m_headerNode = null;
    
	public StructureLoopNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
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

	public void draw(Display display, GC gc, boolean isWorstCase, boolean isBestCase) {
		if (!isVisible()) {
			return;
		}
		drawThis(display, gc);

        drawHeader(display, gc, isWorstCase, isBestCase);
        
        int headerY = scaleY(m_headerNode.getPixelOffsetY() + (m_headerNode.getPixelsY() / 2));
        int headerOutX = scaleX(m_headerNode.getPixelOffsetX() + m_headerNode.getPixelsX() + (m_gapX / 2));
        int headerInX = scaleX(m_headerNode.getPixelOffsetX() - (m_gapX / 2));
        int loopReturnX = scaleX(m_pixelOffsetX + getPixelsX() - (m_gapX / 2));
        int loopReturnY = scaleY(m_pixelOffsetY + getPixelsY() - (m_gapY / 2));

        boolean loopWorstCase = false; 
        boolean loopBestCase = false; 
        if (getChildren().size() > 0) {
	        int childMinY = 0;
	        int childMaxY = 0;
	    	for (ViewNode child : getChildren()) {
				boolean childIsWorstCase = isWorstCase && (child.getTreeNode().getAttributes().getTimeTaken(Case.WORST, false) > 0);
				boolean childIsBestCase = isBestCase && (child.getTreeNode().getAttributes().getTimeTaken(Case.BEST, false) > 0);
	            int childY = scaleY(child.getPixelOffsetY() + (child.getPixelsY() / 2));
	            int childLeft = scaleX(child.getPixelOffsetX());
	            int childRight = scaleX(child.getPixelOffsetX() + child.getPixelsX());

	        	drawConnection(display, gc, headerOutX, childY, childLeft, childY, childIsWorstCase, childIsBestCase);
	    		drawConnection(display, gc, childRight, childY, loopReturnX, childY, childIsWorstCase, childIsBestCase);
	    		
	    		if (childMinY == 0) {
	    			childMinY = childY;
	    		}
	    		childMaxY = childY;
	    		
	    		child.draw(display, gc, childIsWorstCase, childIsBestCase);
	    		loopWorstCase |= childIsWorstCase;
	    		loopBestCase |= childIsBestCase;
	    	}
	    	
			drawConnection(display, gc, headerOutX, headerY, headerOutX, childMaxY, loopWorstCase, loopBestCase);
			drawConnection(display, gc, loopReturnX, childMinY, loopReturnX, loopReturnY, loopWorstCase, loopBestCase);
			drawConnection(display, gc, headerInX, loopReturnY, loopReturnX, loopReturnY, loopWorstCase, loopBestCase);
        } else {
        	loopWorstCase = m_headerNode.getTreeNode().getAttributes().getNumIterations(Case.WORST) > 0;
			drawConnection(display, gc, headerOutX, headerY, headerOutX, loopReturnY, loopWorstCase, loopBestCase);
			drawConnection(display, gc, headerInX, loopReturnY, headerOutX, loopReturnY, loopWorstCase, loopBestCase);
        }
		drawConnection(display, gc, headerInX, headerY, headerInX, loopReturnY, loopWorstCase, loopBestCase);
		
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

	private void drawHeader(Display display, GC gc, boolean isWorstCase, boolean isBestCase) {
        m_headerNode.draw(display, gc, isWorstCase, isBestCase);

        // Wire through the header
        int midY = scaleY(m_pixelOffsetY + (getPixelsY() / 2));
        int right = m_x + m_width;
        int headerY = scaleY(m_headerNode.getPixelOffsetY() + (m_headerNode.getPixelsY() / 2));
        int headerRight = scaleX(m_headerNode.getPixelOffsetX() + m_headerNode.getPixelsX());

    	drawConnection(display, gc, m_x, midY, m_x, headerY, isWorstCase, isBestCase);
		drawConnection(display, gc, m_x, headerY, scaleX(m_headerNode.getPixelOffsetX()), headerY, isWorstCase, isBestCase);
		drawConnection(display, gc, headerRight, headerY, right, headerY, isWorstCase, isBestCase);
		drawConnection(display, gc, right, midY, right, headerY, isWorstCase, isBestCase);
	}

	public void drawTimes(Display display, GC gc, boolean isWorstCase, boolean isBestCase) {
		if (!isVisibleX()) {
			return;
		}
		
		m_headerNode.drawTimes(display, gc, isWorstCase, isBestCase);
		super.drawTimes(display, gc, isWorstCase, isBestCase);
	}

	public void updateXy() {
		m_headerNode.updateXy();
		super.updateXy();
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
		super.buildClickAreas(clickAreas);
	}

	public void addChild(ViewNode child) {
		if (m_headerNode == null) {
			m_headerNode = child;
		} else {
			super.addChild(child);
		}
	}

	public boolean isFolded() {
		return false;
	}
}
