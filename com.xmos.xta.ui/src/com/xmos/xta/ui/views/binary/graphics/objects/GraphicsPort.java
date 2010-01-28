package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class GraphicsPort extends GraphicsObject {

	private String m_name;
	private String m_xnName;

    public GraphicsPort(String name, String xnName, int node, int core) {
    	m_name = name;
    	m_xnName = xnName;
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
			return String.format("%s (%s)", m_xnName, m_name);
		} else {
			return m_name;
		}
	}

	@Override
	public String getToolTip() {
		return getName();
	}
}
