package com.xmos.xta.gui.views.viewnode;

import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class StructureSectionNode extends StructureNode {
    public static final boolean DEBUG = false;

    private int m_borderX = 4;
    private int m_borderY = 2;
    private int m_gapY = 12;
    
    private boolean m_isOptional;
    
	public StructureSectionNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
		
		m_isOptional = m_treeNode.isSectionOptional();
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

	public void draw(Display display, GC gc) {
		if (!isVisible())
			return;

		drawThis(display, gc);
		boolean isBestCase = isBestCase();
		boolean isWorstCase = isWorstCase();
		boolean verticalIsBestCase = false;
		boolean verticalIsWorstCase = false;

        int midY = scaleY(m_pixelOffsetY + (getPixelsY() / 2));
    	int childOffsetY = m_borderY;
    	List<ViewNode> children = getChildren();
		for (int i = 0; i < children.size(); i++) {
    		ViewNode child = children.get(i);
			boolean childIsBestCase = isBestCase && child.isBestCase();
			boolean childIsWorstCase = isWorstCase && child.isWorstCase();

    		int childY = child.getPixelsY();
    		int childMidY = scaleY(m_pixelOffsetY + childOffsetY + (childY / 2));

    		// Ensure the worst/best case lines join up all the way above the midpoint in sections
    		// otherwise they are overwritten by later children
    		if (childMidY > midY || i == 0) {
    			verticalIsBestCase = childIsBestCase;
    			verticalIsWorstCase = childIsWorstCase;
    		} else {
    			verticalIsBestCase |= childIsBestCase;
    			verticalIsWorstCase |= childIsWorstCase;
    		}

        	int childLeft = scaleX(child.getPixelOffsetX());
        	int childRight = scaleX(child.getPixelOffsetX() + child.getPixelsX());
        	drawConnection(display, gc, m_renderX, midY, m_renderX, childMidY,
        			verticalIsBestCase, verticalIsWorstCase);
        	drawConnection(display, gc, m_renderX, childMidY, childLeft, childMidY,
        			childIsBestCase, childIsWorstCase);
    		drawConnection(display, gc, m_renderX + (int)m_width, midY, m_renderX + (int)m_width, childMidY,
    				verticalIsBestCase, verticalIsWorstCase);
    		drawConnection(display, gc, m_renderX + (int)m_width, childMidY, childRight, childMidY,
    				childIsBestCase, childIsWorstCase);
            childOffsetY += childY + m_gapY;

            child.draw(display, gc);
    	}

		if (m_isOptional) {
			int optionalMid = scaleY(m_pixelOffsetY + childOffsetY - (m_gapY / 2));
			drawConnection(display, gc, m_renderX, midY, m_renderX, optionalMid,
					isBestCase, false);
			drawConnection(display, gc, m_renderX, optionalMid, m_renderX + (int)m_width, optionalMid,
					isBestCase, false);
			drawConnection(display, gc, m_renderX + (int)m_width, midY, m_renderX + (int)m_width, optionalMid,
					isBestCase, false);
		}
	}

	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureSection (%d, %d)\n", offsetX, offsetY);

		m_pixelOffsetX = offsetX;
		m_pixelOffsetY = offsetY;

		SectionNode sectionNode = (SectionNode)m_treeNode;
		Node bestCaseNode = sectionNode.getOption(Case.BEST, false);
		Node worstCaseNode = sectionNode.getOption(Case.WORST, false);
		
    	int childOffsetY = m_borderY;
    	int childrenX = getChildrenX();
		for (ViewNode child : getChildren()) {
			Node childTreeNode = child.getTreeNode();
			boolean childIsBestCase = isBestCase && (childTreeNode == bestCaseNode);
			boolean childIsWorstCase = isWorstCase && (childTreeNode == worstCaseNode);

			int childX = child.getPixelsX();
    		child.annotateNodes(offsetX + m_borderX + ((childrenX - childX) / 2), offsetY + childOffsetY,
    				childIsBestCase, childIsWorstCase);
    		childOffsetY += child.getPixelsY() + m_gapY;
    	}

		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}
}
