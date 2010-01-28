package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class GraphicsPortBit extends GraphicsObject {

	private String m_name;
	private String m_xnName;
	private int m_node;
	private int m_core;
	private int m_bitNum;

    public GraphicsPortBit(String name, String xnName, int node, int core, int bitNum) {
    	m_name = name;
    	m_xnName = xnName;
    	m_node = node;
    	m_core = core;
    	m_bitNum = bitNum;
    }
    
    public int getNode() {
    	return m_node;
    }

    public int getCore() {
    	return m_core;
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
		return "port";
	}

	@Override
	public String getName() {
		if (m_xnName != null) {
			return String.format("%s[%d]", m_xnName, m_bitNum);
		} else {
			return String.format("%s[%d]", m_name, m_bitNum);
		}
	}

	@Override
	public String getToolTip() {
		return getName() + String.format(" (node %d, core %d)", m_node, m_core);
	}

	public String getPortName() {
		return m_name;
	}
}
