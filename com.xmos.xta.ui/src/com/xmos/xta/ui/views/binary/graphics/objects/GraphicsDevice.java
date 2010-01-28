package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.libs.proginfoloader.ProgInfoEntry;

public class GraphicsDevice extends GraphicsObject {

    static final int NAME_PADDING = 5;
    static final int CORNER_ROUNDING = 7;
    private static final int BORDER_X = 5;
    private static final int BORDER_Y = 5;

    private GraphicsPins m_pins;
    private Map<Integer, GraphicsNode> m_nodes;
    private String m_name;

    public GraphicsDevice(String name) {
    	m_name = name;
    	m_nodes = new TreeMap<Integer, GraphicsNode>();
    	m_pins = new GraphicsPins();
    	
    	addChild(m_pins);
    }
    
	@Override
    public int getPixelsX(boolean summary) {
		if (summary) {
			return getInfoPixelsX();
		} else {
			int pixelsX = 0;
			for (GraphicsNode node : m_nodes.values()) {
				pixelsX += node.getPixelsX(summary) + BORDER_X;
			}
			return pixelsX + (BORDER_X * 2);
		}
    }
    
	@Override
	public int getPixelsY(boolean summary) {
		if (summary) {
			return getInfoPixelsY();
		} else {
			int pixelsY = 0;
			for (GraphicsNode node : m_nodes.values()) {
				pixelsY = Math.max(pixelsY, node.getPixelsY(summary));
			}
			return pixelsY + (BORDER_Y * 2);
		}
    }
    
	@Override
	public void draw(Display display, GC gc, int x, int y, double scale, boolean summary) {
		track(x, y);

		if (summary) {
			drawInfo(display, gc, x, y, scale, summary);
		} else {
			drawNodes(display, gc, x, y, scale);
		}
    }
    
	public void addInfo(Display display, ProgInfoEntry info) {
		super.addInfo(display, info);

		int nodeId = info.getNode();
		GraphicsNode node = m_nodes.get(nodeId);
		if (node != null) {
			node.addInfo(display, info);
		}
	}

	public void addPin(String name, GraphicsPin pin) {
		m_pins.addPin(name, pin);
	}

	public void addNode(int nodeId, GraphicsNode node) {
		assert(m_nodes.containsKey(nodeId) == false);
		m_nodes.put(nodeId, node);
		addChild(node);
	}

	private void drawNodes(Display display, GC gc, int x, int y, double scale) {
    	if (m_nodes.isEmpty()) {
    		return;
    	}

    	int offsetX = BORDER_X;
    	int offsetY = BORDER_Y;
    	
		for (GraphicsNode node : m_nodes.values()) {
			node.draw(display, gc, offsetX, offsetY, scale, false);
			offsetX += node.getPixelsX(false) + BORDER_X;
		}
	}

	@Override
	public String getIconName() {
		return "device";
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String getToolTip() {
		return getName();
	}

	public GraphicsPort getPort(int nodeNumber, int coreNumber, String name) {
		GraphicsNode node = m_nodes.get(nodeNumber);
		if (node != null) {
			return node.getPort(coreNumber, name);
		}
		return null;
	}

	public GraphicsNode getNode(int nodeNumber) {
		return m_nodes.get(nodeNumber);
	}

	public GraphicsPin getPin(String name) {
		return m_pins.getPin(name);
	}

	public GraphicsPins getPins() {
		return m_pins;
	}
}
