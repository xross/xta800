package com.xmos.xta.rcp.views.ViewNode;

import java.util.ArrayList;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.rcp.nodes.Node;

public class FunctionRouteNode extends ViewNode {
    public static final boolean DEBUG = false;

	public FunctionRouteNode(Node treeNode) {
		super(treeNode);

		m_pixelsX = 0;
		m_pixelsY = 0;
	}

	public int getPixelsX() {
		return 0;
	}

	public int getPixelsY() {
		return 0;
	}

	public int getChildrenX() {
		return 0;
	}

	public int getChildrenY() {
		return 0;
	}
	
	public void draw(Display display, GC gc, double scale) {
		// Should never happen
		assert(false);
	}

	public void annotateXy(int xOffset, int yOffset) {
		// Should never happen
		assert(false);
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
		for (Node treeNode : m_treeNode.getNodes()) {
			ViewNode child = treeNode.getFunctionNode();
    		child.buildClickAreas(clickAreas);
    	}
	}

	public boolean contains(int x, int y) {
		return false;
	}
}
