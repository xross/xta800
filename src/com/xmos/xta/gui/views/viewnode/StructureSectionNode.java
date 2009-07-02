package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.gui.views.structure.ZoomedCanvasProperties;

public class StructureSectionNode extends ViewNode {
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

	public void draw(Display display, GC structureGc, GC timesGc) {
		drawThis(display, structureGc);
		boolean isWorstCase = isWorstCase();
		boolean isBestCase = isBestCase();

        int midY = scale(m_pixelOffsetY + (getPixelsY() / 2));
    	int childOffsetY = m_borderY;
		for (ViewNode child : getChildren()) {
			boolean childIsWorstCase = isWorstCase && child.isWorstCase();
			boolean childIsBestCase = isBestCase && child.isBestCase();

    		int childY = child.getPixelsY();
    		int childMid = scale(m_pixelOffsetY + childOffsetY + (childY / 2));

        	int childLeft = scale(child.getPixelOffsetX());
        	int childRight = scale(child.getPixelOffsetX() + child.getPixelsX());
        	drawConnection(display, structureGc, m_x, midY, m_x, childMid, childIsWorstCase, childIsBestCase);
        	drawConnection(display, structureGc, m_x, childMid, childLeft, childMid, childIsWorstCase, childIsBestCase);
    		drawConnection(display, structureGc, m_x + m_width, midY, m_x + m_width, childMid, childIsWorstCase, childIsBestCase);
    		drawConnection(display, structureGc, m_x + m_width, childMid, childRight, childMid, childIsWorstCase, childIsBestCase);
            childOffsetY += childY + m_gapY;

            child.draw(display, structureGc, timesGc);
    	}

		if (m_isOptional) {
			int optionalMid = scale(m_pixelOffsetY + childOffsetY - (m_gapY / 2));
			drawConnection(display, structureGc, m_x, midY, m_x, optionalMid, false, isBestCase);
			drawConnection(display, structureGc, m_x, optionalMid, m_x + m_width, optionalMid, false, isBestCase);
			drawConnection(display, structureGc, m_x + m_width, midY, m_x + m_width, optionalMid, false, isBestCase);
		}
	}

	public void annotateNodes(int xOffset, int yOffset, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureSection (%d, %d)\n", xOffset, yOffset);

		m_pixelOffsetX = xOffset;
		m_pixelOffsetY = yOffset;

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
    		child.annotateNodes(xOffset + m_borderX + ((childrenX - childX) / 2), yOffset + childOffsetY,
    				childIsBestCase, childIsWorstCase);
    		childOffsetY += child.getPixelsY() + m_gapY;
    	}

		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}

	public boolean isFolded() {
		return false;
	}
}
