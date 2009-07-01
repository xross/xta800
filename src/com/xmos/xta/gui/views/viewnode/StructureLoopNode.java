package com.xmos.xta.gui.views.viewnode;

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

	public void draw(Display display, GC gc) {
		drawThis(display, gc);

		boolean isWorstCase = isWorstCase();
		boolean isBestCase = isBestCase();
        drawHeader(display, gc, isWorstCase, isBestCase);
        
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

	        	drawConnection(display, gc, headerOutX, childY, childLeft, childY, childIsWorstCase, childIsBestCase);
	    		drawConnection(display, gc, childRight, childY, loopReturnX, childY, childIsWorstCase, childIsBestCase);
	    		
	    		if (childMinY == 0) {
	    			childMinY = childY;
	    		}
	    		childMaxY = childY;
	    		
	    		child.draw(display, gc);
	    		loopWorstCase |= childIsWorstCase;
	    		loopBestCase |= childIsBestCase;
	    	}
	    	
			drawConnection(display, gc, headerOutX, headerY, headerOutX, childMaxY, loopWorstCase, loopBestCase);
			drawConnection(display, gc, loopReturnX, childMinY, loopReturnX, loopReturnY, loopWorstCase, loopBestCase);
			drawConnection(display, gc, headerInX, loopReturnY, loopReturnX, loopReturnY, loopWorstCase, loopBestCase);
        } else {
        	loopWorstCase = headerNode.getTreeNode().getAttributes().getNumIterations(Case.WORST) > 0;
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
        ViewNode headerNode = getHeaderNode();
        headerNode.draw(display, gc);

        // Wire through the header
        int midY = scale(m_pixelOffsetY + (getPixelsY() / 2));
        int right = m_x + m_width;
        int headerY = scale(headerNode.getPixelOffsetY() + (headerNode.getPixelsY() / 2));
        int headerRight = scale(headerNode.getPixelOffsetX() + headerNode.getPixelsX());

    	drawConnection(display, gc, m_x, midY, m_x, headerY, isWorstCase, isBestCase);
		drawConnection(display, gc, m_x, headerY, scale(headerNode.getPixelOffsetX()), headerY, isWorstCase, isBestCase);
		drawConnection(display, gc, headerRight, headerY, right, headerY, isWorstCase, isBestCase);
		drawConnection(display, gc, right, midY, right, headerY, isWorstCase, isBestCase);
	}

	public void annotateXy(int xOffset, int yOffset) {
		if (DEBUG) System.out.printf("Annotate StructureLoop (%d, %d\n", xOffset, yOffset);

		m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;
		
        ViewNode headerNode = getHeaderNode();
		headerNode.annotateXy(xOffset + m_borderX + m_gapX, yOffset + m_borderY);

        int childOffsetX = m_borderX + headerNode.getPixelsX() + (2 * m_gapX);
        int childOffsetY = m_borderY + headerNode.getPixelsY() + m_gapY;
		for (int i = 1; i < getChildren().size(); i++) {
			ViewNode child = getChildren().get(i);
    		child.annotateXy(xOffset + childOffsetX, yOffset + childOffsetY);
    		childOffsetY += child.getPixelsY() + m_gapY;
    	}
	}

	public boolean isFolded() {
		return false;
	}
}
