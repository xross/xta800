package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.structure.ZoomedCanvasProperties;

public class StructureLoopNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 8;
    private int m_borderY = 4;
    private int m_gapX = 8;
    private int m_gapY = 4;
    
	public StructureLoopNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}
	
	private ViewNode getHeaderNode() {
		assert(getChildren().size() > 0);
		return getChildren().get(0);
	}

	public int getPixelsX() {
		return getChildrenX() + m_gapX + (2 * m_borderX);
	}

	public int getPixelsY() {
		return getChildrenY() + (2 * m_borderY);
	}

	public int getChildrenX() {
		int pixels = getHeaderNode().getPixelsX() + m_gapX;

		int childrenX = 0;
		for (int i = 1; i < getChildren().size(); i++) {
			ViewNode child = getChildren().get(i);
			int childX = child.getPixelsX();
			if (childX > childrenX)
				childrenX = childX;
		}
		return pixels + childrenX;
	}

	public int getChildrenY() {
		int pixels = getHeaderNode().getPixelsY() + m_gapY;
		for (int i = 1; i < getChildren().size(); i++) {
			ViewNode child = getChildren().get(i);
			pixels += child.getPixelsY() + m_gapY;
		}
		return pixels;
	}

	public void draw(Display display, GC structureGc, GC timesGc) {
		drawThis(display, structureGc);

		boolean isWorstCase = isWorstCase();
		boolean isBestCase = isBestCase();
        drawHeader(display, structureGc, timesGc, isWorstCase, isBestCase);
        
        ViewNode headerNode = getHeaderNode();
        int headerY = scale(headerNode.getPixelOffsetY() + (headerNode.getPixelsY() / 2));
        int headerOutX = scale(headerNode.getPixelOffsetX() + headerNode.getPixelsX() + (m_gapX / 2));
        int headerInX = scale(headerNode.getPixelOffsetX() - (m_gapX / 2));
        int loopReturnX = scale(m_pixelOffsetX + getPixelsX() - (m_gapX / 2));
        int loopReturnY = scale(m_pixelOffsetY + getPixelsY() - (m_gapY / 2));

        boolean loopWorstCase = false; 
        boolean loopBestCase = false; 
        if (getChildren().size() > 1) {
	        int childMinY = 0;
	        int childMaxY = 0;
			for (int i = 1; i < getChildren().size(); i++) {
				ViewNode child = getChildren().get(i);
				boolean childIsWorstCase = isWorstCase && child.isWorstCase();
				boolean childIsBestCase = isBestCase && child.isBestCase();
	            int childY = scale(child.getPixelOffsetY() + (child.getPixelsY() / 2));
	            int childLeft = scale(child.getPixelOffsetX());
	            int childRight = scale(child.getPixelOffsetX() + child.getPixelsX());

	        	drawConnection(display, structureGc, headerOutX, childY, childLeft, childY, childIsWorstCase, childIsBestCase);
	    		drawConnection(display, structureGc, childRight, childY, loopReturnX, childY, childIsWorstCase, childIsBestCase);
	    		
	    		if (childMinY == 0) {
	    			childMinY = childY;
	    		}
	    		childMaxY = childY;
	    		
	    		child.draw(display, structureGc, timesGc);
	    		loopWorstCase |= childIsWorstCase;
	    		loopBestCase |= childIsBestCase;
	    	}
	    	
			drawConnection(display, structureGc, headerOutX, headerY, headerOutX, childMaxY, loopWorstCase, loopBestCase);
			drawConnection(display, structureGc, loopReturnX, childMinY, loopReturnX, loopReturnY, loopWorstCase, loopBestCase);
			drawConnection(display, structureGc, headerInX, loopReturnY, loopReturnX, loopReturnY, loopWorstCase, loopBestCase);
        } else {
        	loopWorstCase = m_isWorstCase && headerNode.getTreeNode().getAttributes().getLoopIterations() > 0;
			drawConnection(display, structureGc, headerOutX, headerY, headerOutX, loopReturnY, loopWorstCase, loopBestCase);
			drawConnection(display, structureGc, headerInX, loopReturnY, headerOutX, loopReturnY, loopWorstCase, loopBestCase);
        }
		drawConnection(display, structureGc, headerInX, headerY, headerInX, loopReturnY, loopWorstCase, loopBestCase);
		
		structureGc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		int[] outTriangle = {
				headerOutX, headerY + (2 * m_borderY),
				headerOutX + (m_borderX / 2), headerY + m_borderY,
				headerOutX - (m_borderX / 2), headerY + m_borderY,};
		structureGc.fillPolygon(outTriangle);

		int[] returnTriangle = {
				headerInX, headerY + m_borderY,
				headerInX + (m_borderX / 2), headerY + (2 * m_borderY),
				headerInX - (m_borderX / 2), headerY + (2 * m_borderY),};
		structureGc.fillPolygon(returnTriangle);
	}

	private void drawHeader(Display display, GC structureGc, GC timesGc, boolean isWorstCase, boolean isBestCase) {
        ViewNode headerNode = getHeaderNode();
        headerNode.draw(display, structureGc, timesGc);

        // Wire through the header
        int midY = scale(m_pixelOffsetY + (getPixelsY() / 2));
        int right = m_x + m_width;
        int headerY = scale(headerNode.getPixelOffsetY() + (headerNode.getPixelsY() / 2));
        int headerRight = scale(headerNode.getPixelOffsetX() + headerNode.getPixelsX());

    	drawConnection(display, structureGc, m_x, midY, m_x, headerY, isWorstCase, isBestCase);
		drawConnection(display, structureGc, m_x, headerY, scale(headerNode.getPixelOffsetX()), headerY, isWorstCase, isBestCase);
		drawConnection(display, structureGc, headerRight, headerY, right, headerY, isWorstCase, isBestCase);
		drawConnection(display, structureGc, right, midY, right, headerY, isWorstCase, isBestCase);
	}

	public void annotateNodes(int xOffset, int yOffset, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureLoop (%d, %d\n", xOffset, yOffset);

		m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;
		
        ViewNode headerNode = getHeaderNode();
		headerNode.annotateNodes(xOffset + m_borderX + m_gapX, yOffset + m_borderY, isBestCase, isWorstCase);

        int childOffsetX = m_borderX + headerNode.getPixelsX() + (2 * m_gapX);
        int childOffsetY = m_borderY + headerNode.getPixelsY() + m_gapY;
		for (int i = 1; i < getChildren().size(); i++) {
			ViewNode child = getChildren().get(i);
    		child.annotateNodes(xOffset + childOffsetX, yOffset + childOffsetY, isBestCase, isWorstCase);
    		childOffsetY += child.getPixelsY() + m_gapY;
    	}

		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}

	public boolean isFolded() {
		return false;
	}
}
