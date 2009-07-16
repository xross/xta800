package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class StructureLoopNode extends StructureNode {
    public static final boolean DEBUG = false;

    private boolean m_isInfinite;
    private int m_gapX = 8;
    private int m_gapY = 4;
    
	public StructureLoopNode(Node treeNode, ZoomedCanvasProperties canvasProperties, boolean isInfinite) {
		super(treeNode, canvasProperties);
	    m_borderX = 8;
	    m_borderY = 4;
		m_isInfinite = isInfinite;
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
		if (!isVisible())
			return;

		drawThis(display, gc);

		boolean isWorstCase = isWorstCase();
		boolean isBestCase = isBestCase();

        ViewNode headerNode = getHeaderNode();
        int headerY = scaleY(headerNode.getPixelOffsetY() + (headerNode.getPixelsY() / 2));
        int headerOutX = scaleX(headerNode.getPixelOffsetX() + headerNode.getPixelsX() + (m_gapX / 2));
        int headerInX = scaleX(headerNode.getPixelOffsetX() - (m_gapX / 2));
        int loopReturnX = scaleX(m_pixelOffsetX + getPixelsX() - (m_gapX / 2));
        int loopReturnY = scaleY(m_pixelOffsetY + getPixelsY() - (m_gapY / 2));
        
        drawHeader(display, gc, isWorstCase, isBestCase, headerOutX);
        
        boolean loopWorstCase = false; 
        boolean loopBestCase = false; 
        if (getChildren().size() > 1) {
	        int childMinY = 0;
	        int childMaxY = 0;
			for (int i = 1; i < getChildren().size(); i++) {
				ViewNode child = getChildren().get(i);
				boolean childIsWorstCase = isWorstCase && child.isWorstCase();
				boolean childIsBestCase = isBestCase && child.isBestCase();
	            int childY = scaleY(child.getPixelOffsetY() + (child.getPixelsY() / 2));
	            int childLeft = scaleX(child.getPixelOffsetX());
	            int childRight = scaleX(child.getPixelOffsetX() + child.getPixelsX());

	        	drawConnection(display, gc, headerOutX, childY, childLeft, childY,
	        			childIsBestCase, childIsWorstCase);
	    		drawConnection(display, gc, childRight, childY, loopReturnX, childY,
	    				childIsBestCase, childIsWorstCase);
	    		
	    		if (childMinY == 0) {
	    			childMinY = childY;
	    		}
	    		childMaxY = childY;
	    		
	    		child.draw(display, gc);
	    		loopWorstCase |= childIsWorstCase;
	    		loopBestCase |= childIsBestCase;
	    	}
	    	
			drawConnection(display, gc, headerOutX, headerY, headerOutX, childMaxY,
					loopBestCase, loopWorstCase);
			drawConnection(display, gc, loopReturnX, childMinY, loopReturnX, loopReturnY,
					loopBestCase, loopWorstCase);
			drawConnection(display, gc, headerInX, loopReturnY, loopReturnX, loopReturnY,
					loopBestCase, loopWorstCase);
        } else {
        	loopWorstCase = isWorstCase && (headerNode.getTreeNode().getAttributes().getTotalIterations(Case.WORST, null) > 1);
			drawConnection(display, gc, headerOutX, headerY, headerOutX, loopReturnY,
					loopBestCase, loopWorstCase);
			drawConnection(display, gc, headerInX, loopReturnY, headerOutX, loopReturnY,
					loopBestCase, loopWorstCase);
        }
		drawConnection(display, gc, headerInX, headerY, headerInX, loopReturnY,
				loopBestCase, loopWorstCase);
		
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

		if (hasUnknowns()) {
    		gc.drawImage(m_warningImage,
    				headerOutX - m_warningX / 2, 
    				loopReturnY - m_warningY / 2);
        }
	}

	private boolean hasUnknowns() {
		NodeAttributes nodeAttributes = m_treeNode.getAttributes();
		if (nodeAttributes.isUnknown() &&
			(nodeAttributes.getUnknownState() == UnknownState.UNKNOWN)) {
			return true;
		}
		return false;
	}

	private void drawHeader(Display display, GC gc, boolean isWorstCase, boolean isBestCase, int headerOutX) {
        ViewNode headerNode = getHeaderNode();
        headerNode.draw(display, gc);

        // Wire through the header
        int midY = scaleY(m_pixelOffsetY + (getPixelsY() / 2));
        int right = m_renderX + (int)m_width;
        int headerY = scaleY(headerNode.getPixelOffsetY() + (headerNode.getPixelsY() / 2));
        int headerRight = scaleX(headerNode.getPixelOffsetX() + headerNode.getPixelsX());

    	drawConnection(display, gc, m_renderX, midY, m_renderX, headerY,
    			isBestCase, isWorstCase);
		drawConnection(display, gc, m_renderX, headerY, scaleX(headerNode.getPixelOffsetX()), headerY,
				isBestCase, isWorstCase);
		
		if (m_isInfinite) {
			drawConnection(display, gc, headerRight, headerY, headerOutX, headerY,
					isBestCase, isWorstCase);
		} else {
			drawConnection(display, gc, headerRight, headerY, right, headerY,
					isBestCase, isWorstCase);
			drawConnection(display, gc, right, midY, right, headerY,
					isBestCase, isWorstCase);
			
		}
	}

	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureLoop (%d, %d\n", offsetX, offsetY);

		m_pixelOffsetX = offsetX;
		m_pixelOffsetY = offsetY;
		
        ViewNode headerNode = getHeaderNode();
		headerNode.annotateNodes(offsetX + m_borderX + m_gapX, offsetY + m_borderY, isBestCase, isWorstCase);

        int childOffsetX = m_borderX + headerNode.getPixelsX() + (2 * m_gapX);
        int childOffsetY = m_borderY + headerNode.getPixelsY() + m_gapY;
		for (int i = 1; i < getChildren().size(); i++) {
			ViewNode child = getChildren().get(i);
			
			boolean childIsBestCase = isBestCase && (child.getTreeNode().getAttributes().getTotalIterations(Case.BEST, null) > 0);
			boolean childIsWorstCase = isBestCase && (child.getTreeNode().getAttributes().getTotalIterations(Case.WORST, null) > 0);
			child.annotateNodes(offsetX + childOffsetX, offsetY + childOffsetY, childIsBestCase, childIsWorstCase);
    		childOffsetY += child.getPixelsY() + m_gapY;
    	}

		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}

    public boolean isInfinite() {
    	return m_isInfinite;
	}
}
