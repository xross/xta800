package com.xmos.xta.ui.views.visualisations.viewnode;

import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.ui.views.visualisations.ZoomedCanvasProperties;

public class StructureConditionalNode extends StructureNode {
    public static final boolean DEBUG = false;

    private int m_gapY = 12;
    
	int m_bestCaseY;
	int m_worstCaseY;
	boolean m_bestCaseInfinite;
	boolean m_worstCaseInfinite;
    
	public StructureConditionalNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	    m_borderX = 4;
	    m_borderY = 2;
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
		
		return pixels;
	}

	public void draw(Display display, GC gc) {
		if (!isVisible())
			return;

		drawThis(display, gc);
		boolean isBestCase = isBestCase();
		boolean isWorstCase = isWorstCase();
		
		boolean drawn = false;
		clearBestWorstY();
		
        int midY = scaleY(m_pixelOffsetY + (getPixelsY() / 2));
    	int childOffsetY = m_borderY;
    	List<ViewNode> children = getChildren();
		for (int i = 0; i < children.size(); i++) {
    		ViewNode child = children.get(i);
    		boolean childIsInfinite = child.isInfinite();
			boolean childIsBestCase = isBestCase && child.isBestCase();
			boolean childIsWorstCase = isWorstCase && child.isWorstCase();

    		int childY = child.getPixelsY();
    		int childMidY = scaleY(m_pixelOffsetY + childOffsetY + (childY / 2));

    		if (childMidY > midY && !drawn) {
    			drawBestWorstCaseConnections(display, gc, midY);
    			clearBestWorstY();
    			drawn = true;
    		}
    		
    		trackBestWorstY(midY, childMidY, childIsBestCase, childIsWorstCase, childIsInfinite);

        	int childLeft = scaleX(child.getPixelOffsetX());
        	int childRight = scaleX(child.getPixelOffsetX() + child.getPixelsX());
        	drawConnection(display, gc, m_renderX, midY, m_renderX, childMidY,
        			false, false);
        	drawConnection(display, gc, m_renderX, childMidY, childLeft, childMidY,
        			childIsBestCase, childIsWorstCase);
        	
        	if (!childIsInfinite) {
	    		drawConnection(display, gc, m_renderX + (int)m_width, midY, m_renderX + (int)m_width, childMidY,
	    				false, false);
	    		drawConnection(display, gc, m_renderX + (int)m_width, childMidY, childRight, childMidY,
	    				childIsBestCase, childIsWorstCase);
        	}
            childOffsetY += childY + m_gapY;

            child.draw(display, gc);
    	}
		
		drawBestWorstCaseConnections(display, gc, midY);
	}

	private void trackBestWorstY(int midY, int childMidY, boolean childIsBestCase, boolean childIsWorstCase,
			boolean childIsInfinite) {

		if (childIsBestCase) {
			if ((childMidY > midY) || (m_bestCaseY == 0)) {
				m_bestCaseY = childMidY;
				m_bestCaseInfinite = childIsInfinite;
			}
		}
		if (childIsWorstCase) {
			if ((childMidY > midY) || (m_worstCaseY == 0)) {
				m_worstCaseY = childMidY;
				m_worstCaseInfinite = childIsInfinite;
			}
		}
	}

	private void clearBestWorstY() {
		m_bestCaseY = 0;
		m_worstCaseY = 0;
		m_bestCaseInfinite = false;
		m_worstCaseInfinite = false;
	}

	private void drawBestWorstCaseConnections(Display display, GC gc, int midY) {
		// Draw the connections to the best/worst-case option
		drawBestWorstCaseConnections(display, gc, m_renderX, midY, m_bestCaseY, m_worstCaseY);

		// Draw the return from the best/worst-case options where needed
		drawBestWorstCaseConnections(display, gc, m_renderX + (int)m_width, midY,
				m_bestCaseInfinite ? 0 : m_bestCaseY, m_worstCaseInfinite ? 0 : m_worstCaseY);
	}

	private void drawBestWorstCaseConnections(Display display, GC gc, int x, int midY, int bestCaseY, int worstCaseY) {
		if (casesSameSide(midY, bestCaseY, worstCaseY)) {
			int closestY = getClosestY(midY, bestCaseY, worstCaseY);
	    	drawConnection(display, gc, x, midY, x, closestY, true, true);
	    	if (closestY == bestCaseY) {
	    		drawConnection(display, gc, x, closestY, x, worstCaseY, false, true);
	    	} else {
	    		drawConnection(display, gc, x, closestY, x, bestCaseY, true, false);
	    	}
		} else {
			if (bestCaseY != 0) {
	    		drawConnection(display, gc, x, midY, x, bestCaseY, true, false);
			}
			
			if (worstCaseY != 0) {
	    		drawConnection(display, gc, x, midY, x, worstCaseY, false, true);
			}
		}
	}

	private int getClosestY(int midY, int bestCaseY, int worstCaseY) {
		if (bestCaseY < midY) {
			if (bestCaseY > worstCaseY) {
				return bestCaseY;
			} else {
				return worstCaseY;
			}
		} else {
			if (bestCaseY < worstCaseY) {
				return bestCaseY;
			} else {
				return worstCaseY;
			}
		}
	}

	private boolean casesSameSide(int midY, int bestCaseY, int worstCaseY) {
		if ((bestCaseY == 0) || (worstCaseY == 0))
			return false;
		
		boolean bestCaseAbove = bestCaseY < midY;
		boolean worstCaseAbove = worstCaseY < midY;
		if (bestCaseAbove == worstCaseAbove)
			return true;

		return false;
	}

	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureCond (%d, %d)\n", offsetX, offsetY);

		m_pixelOffsetX = offsetX;
		m_pixelOffsetY = offsetY;

    	int childOffsetY = m_borderY;
    	int childrenX = getChildrenX();
		for (ViewNode child : getChildren()) {
			Status status = new Status();
			Node treeNode = child.getTreeNode();
			
			double time = treeNode.getAttributes().getTimeTaken(Case.BEST, status, null);
			boolean childIsBestCase = (status.isComplete() && (time > 0)) || (!status.isZero());
			boolean childIsWorstCase = treeNode.getAttributes().getTimeTaken(Case.WORST, status, null) > 0;

			int childX = child.getPixelsX();
    		child.annotateNodes(offsetX + m_borderX + ((childrenX - childX) / 2), offsetY + childOffsetY,
    				childIsBestCase, childIsWorstCase);
    		childOffsetY += child.getPixelsY() + m_gapY;
    	}

		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}
	
    public boolean isInfinite() {
    	// A conditional is only infinite if all children are infinite
    	for (ViewNode child : getChildren()) {
			if (!child.isInfinite()) {
				return false;
			}
    	}
		return true;
	}
}
