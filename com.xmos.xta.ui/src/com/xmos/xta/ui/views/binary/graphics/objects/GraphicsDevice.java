package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.libs.proginfoloader.ProgInfoEntry;

public class GraphicsDevice extends GraphicsObject {

    static final int NAME_PADDING = 5;
    static final int CORNER_ROUNDING = 7;
    private static final int BORDER_X = 10;
    private static final int BORDER_Y = 10;

    private GraphicsPins m_graphicsPins;
    private Map<Integer, List<GraphicsPin>> m_pins;
    private Map<Integer, GraphicsNode> m_nodes;
    private String m_name;

    public GraphicsDevice(String name) {
    	m_name = name;
    	m_pins = new TreeMap<Integer, List<GraphicsPin>>();
		m_pins.put(SWT.TOP, new LinkedList<GraphicsPin>());
		m_pins.put(SWT.RIGHT, new LinkedList<GraphicsPin>());
		m_pins.put(SWT.BOTTOM, new LinkedList<GraphicsPin>());
		m_pins.put(SWT.LEFT, new LinkedList<GraphicsPin>());

    	m_nodes = new TreeMap<Integer, GraphicsNode>();
    	m_graphicsPins = new GraphicsPins();
    	
    	addChild(m_graphicsPins);
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

	public void addPin(int side, GraphicsPin pin) {
		List<GraphicsPin> sidePins = m_pins.get(side);
		assert(sidePins != null);
		sidePins.add(pin);
		
		m_graphicsPins.addChild(pin);
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

/*	private void drawPins(Display display, GC gc, Rectangle area) {
		int leftX = getMaxX(m_pins.get(SWT.LEFT));
    	int pinsX = getTopBottomX();
    	int topY = getMaxY(m_pins.get(SWT.TOP));
    	int pinsY = getLeftRightY();

    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
       	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
       	gc.fillRoundRectangle(BORDER_X + leftX, BORDER_Y + topY, pinsX, pinsY, CORNER_ROUNDING, CORNER_ROUNDING);
    	
		drawSide(display, gc, SWT.TOP, BORDER_X + leftX, BORDER_Y);
		drawSide(display, gc, SWT.RIGHT, BORDER_X + pinsX + leftX, BORDER_Y + topY);
		drawSide(display, gc, SWT.BOTTOM, BORDER_X + leftX, BORDER_Y + pinsY + topY);
		drawSide(display, gc, SWT.LEFT, BORDER_X + leftX, BORDER_Y + topY);
	}

	private void drawSide(Display display, GC gc, int side, int offsetX, int offsetY) {
		for (GraphicsPin pin : m_pins.get(side)) {
			pin.draw(display, gc, new Rectangle(offsetX, offsetY, 0, 0));
			
			if ((side == SWT.LEFT) || (side == SWT.RIGHT)) {
				offsetY += pin.getPixelsY();
			}
			if ((side == SWT.TOP) || (side == SWT.BOTTOM)) {
				offsetX += pin.getPixelsX();
			}
		}
	}

	private int getPinsX() {
    	int x = getMaxX(m_pins.get(SWT.LEFT));
    	x += getMaxX(m_pins.get(SWT.RIGHT));
    	x += getTopBottomX();
    	return x;
	}

	private int getPinsY() {
    	int y = getMaxY(m_pins.get(SWT.TOP));
    	y += getMaxY(m_pins.get(SWT.BOTTOM));
    	y += getLeftRightY();
    	return y;
	}
	
	private int getTopBottomX() {
    	int topX = getSumX(m_pins.get(SWT.TOP));
    	int bottomX = getSumX(m_pins.get(SWT.BOTTOM));
    	int pinsX = topX > bottomX ? topX : bottomX;
		return pinsX;
	}

    private int getLeftRightY() {
    	int leftY = getSumY(m_pins.get(SWT.LEFT));
    	int rightY = getSumY(m_pins.get(SWT.RIGHT));
    	int pinsY = leftY > rightY ? leftY : rightY;
    	return pinsY;
	}

	private int getMaxX(List<GraphicsPin> list) {
    	int x = 0;
    	for (GraphicsPin pin : list) {
    		int pinX = pin.getPixelsX(false);
			if (pinX > x) {
    			x = pinX;
    		}
    	}
    	return x;
	}

    private int getMaxY(List<GraphicsPin> list) {
    	int y = 0;
    	for (GraphicsPin pin : list) {
    		int pinY = pin.getPixelsY(false);
			if (pinY > y) {
    			y = pinY;
    		}
    	}
    	return y;
	}

    private int getSumX(List<GraphicsPin> list) {
    	int x = 0;
    	for (GraphicsPin pin : list) {
    		x += pin.getPixelsX(false);
    	}
    	return x;
	}

    private int getSumY(List<GraphicsPin> list) {
    	int y = 0;
    	for (GraphicsPin pin : list) {
    		y += pin.getPixelsY(false);
    	}
    	return y;
	} */

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
}
