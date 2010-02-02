package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class GraphicsPort extends GraphicsObject {

	private GraphicsDevice m_device;
	private String m_name;
	private String m_xnName;
	private List<Integer> m_portWidths;

    public GraphicsPort(GraphicsDevice device, String name, String xnName, int node, int core) {
    	m_device = device;
    	m_name = name;
    	m_xnName = xnName;
		m_portWidths = new LinkedList<Integer>();
    }
    
	@Override
	public int getPixelsX(boolean summary) {
		for (GraphicsObject child : getChildren()) {
			if (child instanceof GraphicsPin) {
				GraphicsPin pin = m_device.getPin(((GraphicsPin)child).getPinName());
				return pin.getPixelsX(summary, m_portWidths) + PIN_SPACING_X;
			}
		}
		return 0;
    }
    
	@Override
	public int getPixelsY(boolean summary) {
		int y = PIN_SPACING_Y;
		for (GraphicsObject child : getChildren()) {
			if (child instanceof GraphicsPin) {
				GraphicsPin pin = m_device.getPin(((GraphicsPin)child).getPinName());
				y += pin.getPixelsY(summary) + PIN_SPACING_Y;
			}
		}
		return y;
    }
    
	@Override
	public void draw(Display display, GC gc, int x, int y, boolean summary) {
		y += PIN_SPACING_Y;
		int pinNumber = 0;
		for (GraphicsObject child : getChildren()) {
			if (child instanceof GraphicsPin) {
				GraphicsPin pin = m_device.getPin(((GraphicsPin)child).getPinName());
				pin.draw(display, gc, x, y, summary, m_name, pinNumber, m_portWidths);
				y += pin.getPixelsY(summary) + PIN_SPACING_Y;
				pinNumber++;
			}
		}
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
	public String getShortName() {
		return getName();
	}

	@Override
	public String getToolTip() {
		return getName();
	}

	public void addPin(GraphicsPin pin) {
		addChild(pin);

		GraphicsPin realPin = m_device.getPin(pin.getPinName());
		for (int portWidth : realPin.getPortWidths()) {
			if (!m_portWidths.contains(portWidth)) {
				m_portWidths.add(portWidth);
			}
		}
		Collections.sort(m_portWidths);
		Collections.reverse(m_portWidths);
	}
}
