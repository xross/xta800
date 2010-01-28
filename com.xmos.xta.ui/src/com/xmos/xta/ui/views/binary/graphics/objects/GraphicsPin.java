package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class GraphicsPin extends GraphicsObject {

	private String m_name;
	private int m_bitNumber;

    public GraphicsPin(String name) {
		m_name = name;
		m_bitNumber = -1;
    }

    public GraphicsPin(String name, int bitNumber) {
		m_name = name;
		m_bitNumber = bitNumber;
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
		return "instruction_node";
	}

	@Override
	public String getName() {
		if (m_bitNumber >= 0) {
			return String.format("%d - %s", m_bitNumber, m_name);
		} else {
			return m_name;
		}
	}

	@Override
	public String getToolTip() {
		return m_name;
	}

	public String getPinName() {
		return m_name;
	}
}
