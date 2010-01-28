package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class GraphicsLink extends GraphicsObject {

	private int m_node;
	private int m_number;
	private int m_bitNum;
	private String m_dir;

    public GraphicsLink(int node, int number, int bitNum, String dir) {
    	m_node = node;
    	m_number = number;
    	m_bitNum = bitNum;
    	m_dir = dir;
    }
    
	@Override
	public int getPixelsX(boolean summary) {
		return 0;
    }
    
	@Override
	public int getPixelsY(boolean summary) {
		return 0;
    }
    
	@Override
	public void draw(Display display, GC gc, int x, int y, double scale, boolean summary) {
    }

	@Override
	public String getIconName() {
		return m_dir;
	}

	@Override
	public String getName() {
		return String.format("Link%d[%d] (node %d)", m_number, m_bitNum, m_node);
	}

	@Override
	public String getToolTip() {
		return getName();
	}
}
