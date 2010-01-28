package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;


public class GraphicsPins extends GraphicsObject {

	private Map<String, GraphicsPin> m_pins;
	
    public GraphicsPins() {
    	m_pins = new TreeMap<String, GraphicsPin>();
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
	public String getToolTip() {
		return getName();
	}

	@Override
	public void draw(Display display, GC gc, int x, int y, double scale, boolean summary) {
	}

	@Override
	public int getPixelsX(boolean summary) {
		return 0;
	}

	@Override
	public int getPixelsY(boolean summary) {
		return 0;
	}

	public GraphicsObject getObject(int x, int y, boolean summary) {
		return null;
	}

	public void addPin(String name, GraphicsPin pin) {
		m_pins.put(name, pin);
		addChild(pin);
	}

	public GraphicsPin getPin(String name) {
		return m_pins.get(name);
	}
}
