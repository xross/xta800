package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;


public class GraphicsPins extends GraphicsObject {

	private Map<String, GraphicsPin> m_pins;
	private List<Integer> m_portWidths;
	
    public GraphicsPins() {
    	m_pins = new TreeMap<String, GraphicsPin>();
		m_portWidths = new LinkedList<Integer>();
    }

	@Override
	public String getIconName() {
		return "block_node";
	}

	@Override
	public String getName() {
		return String.format("User IOs (%d)", numChildren());
	}

	@Override
	public String getShortName() {
		return getName();
	}

	@Override
	public String getToolTip() {
		return getName();
	}

	@Override
	public void draw(Display display, GC gc, int x, int y, boolean summary) {
		int pinNumber = 0;
		for (GraphicsPin pin : m_pins.values()) {
			pin.draw(display, gc, x + PIN_SPACING_X, y + PIN_SPACING_Y, summary, "", pinNumber, m_portWidths);
			y += pin.getPixelsY(summary) + PIN_SPACING_Y;
			pinNumber++;
		}
	}

	@Override
	public int getPixelsX(boolean summary) {
		if (m_pins.isEmpty()) {
			return 0;
		}
		return firstPin().getPixelsX(summary, m_portWidths);
	}

	@Override
	public GraphicsObject getObject(int x, int y, boolean summary) {
		return super.getObject(x, y, false);
	}

	@Override
	public int getPixelsY(boolean summary) {
		int y = PIN_SPACING_Y;
		for (GraphicsPin pin : m_pins.values()) {
			y += pin.getPixelsY(summary) + PIN_SPACING_Y;
		}
		return y;
	}

	public void addPin(String name, GraphicsPin pin) {
		m_pins.put(name, pin);
		addChild(pin);
		
		for (int portWidth : pin.getPortWidths()) {
			if (!m_portWidths.contains(portWidth)) {
				m_portWidths.add(portWidth);
			}
		}
		Collections.sort(m_portWidths);
		Collections.reverse(m_portWidths);
	}

	public GraphicsPin getPin(String name) {
		return m_pins.get(name);
	}

	private GraphicsPin firstPin() {
		assert(!m_pins.isEmpty());
		return m_pins.values().iterator().next();
	}
}
