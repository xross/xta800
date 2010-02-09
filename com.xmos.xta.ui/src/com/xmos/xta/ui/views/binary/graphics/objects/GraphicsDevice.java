package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.libs.xnloader.XnLoader;

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
			Collection<GraphicsObject> nodes = new LinkedList<GraphicsObject>(m_nodes.values());
			int pixelsX = getPixelsX(nodes, BORDER_X, summary);
			return pixelsX + (BORDER_X * 2);
		}
    }
    
	@Override
	public int getPixelsY(boolean summary) {
		if (summary) {
			return getInfoPixelsY();
		} else {
			Collection<GraphicsObject> nodes = new LinkedList<GraphicsObject>(m_nodes.values());
			int pixelsY = getPixelsY(nodes, BORDER_Y, summary);
			return pixelsY + (BORDER_Y * 2);
		}
    }
    
	@Override
	public void draw(Display display, GC gc, int x, int y, boolean summary) {
		track(x, y);

		if (summary) {
			drawInfo(display, gc, x, y, summary);
		} else {
			Collection<GraphicsObject> nodes = new LinkedList<GraphicsObject>(m_nodes.values());
			draw(display, gc, x, y, summary, nodes, BORDER_X, BORDER_Y);
		}
    }
    
	@Override
	public void addInfo(XnLoader xnLoader, Display display, ProgInfoEntry info) {
		super.addInfo(xnLoader, display, info);

		int nodeNumber = xnLoader.getNodeNumber(info.getNodeRoutingId());
		GraphicsNode node = m_nodes.get(nodeNumber);
		if (node != null) {
			node.addInfo(xnLoader, display, info);
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
	public String getShortName() {
		return getName();
	}

	@Override
	public String getToolTip() {
		return getName();
	}

	public void addPin(String name, GraphicsPin pin) {
		m_pins.addPin(name, pin);
	}

	public void addNode(int nodeNumber, GraphicsNode node) {
		assert(m_nodes.containsKey(nodeNumber) == false);
		m_nodes.put(nodeNumber, node);
		addChild(node);
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
