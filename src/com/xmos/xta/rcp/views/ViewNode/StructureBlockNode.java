package com.xmos.xta.rcp.views.ViewNode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import com.xmos.xta.rcp.nodes.Node;

public class StructureBlockNode extends StructureNode {
    public static final boolean DEBUG = false;

	public StructureBlockNode(Node treeNode) {
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
	}

	public void annotateXy(int xOffset, int yOffset) {
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
	}

	public boolean contains(int x, int y) {
		return false;
	}
}
