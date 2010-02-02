package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.ui.icons.Icons;
import com.xmos.xta.ui.views.binary.graphics.GraphicsUtils;

public class GraphicsPin extends GraphicsObject {

	private String m_name;
	private int m_bitNumber;
	private GraphicsLink m_link;
	private List<GraphicsPortBit> m_portBits;
	private List<Integer> m_portWidths;

    public GraphicsPin(String name) {
		m_name = name;
		m_bitNumber = -1;
		m_portBits = new LinkedList<GraphicsPortBit>();
		m_portWidths = new LinkedList<Integer>();
		m_link = null;
    }

    public GraphicsPin(String name, int bitNumber) {
		m_name = name;
		m_bitNumber = bitNumber;
		m_portBits = new LinkedList<GraphicsPortBit>();
		m_portWidths = new LinkedList<Integer>();
    }
    
	@Override
	public int getPixelsX(boolean summary) {
		return getPixelsX(summary, m_portWidths);
    }
    
	protected int getPixelsX(boolean summary, List<Integer> portWidths) {
		int x = PIN_SPACING_X;

		// Spacing for ports + XLink + pin
		x += (portWidths.size() + 2) * getColumnWidth();
		return x;
	}

	public int getPinPixelsX(boolean summary) {
		return getImagePixelsX() + (2 * PIN_SPACING_X) + (GraphicsUtils.getAverageCharWidth() * m_name.length());
	}

	public int getLinkPixelsX(boolean summary) {
		if (m_link != null) {
			return m_link.getPixelsX(summary);
		}
		return 0;
	}

	@Override
	public int getPixelsY(boolean summary) {
		int y = Math.max(getImagePixelsY(), GraphicsUtils.getTextHeight());
		for (GraphicsObject child : getChildren()) {
			y = Math.max(y, child.getPixelsY(summary));
		}
		return y;
    }
    
	@Override
	public void draw(Display display, GC gc, int x, int y, boolean summary) {
		draw(display, gc, x, y, summary, "", 0, m_portWidths);
	}

	public void draw(Display display, GC gc, int x, int y, boolean summary, String portName,
			int row, List<Integer> portWidths) {
		
		int width = getPixelsX(summary, portWidths);
		int rowHeight = getPixelsY(summary);
		drawRow(display, gc, row, y, width, rowHeight);
		
		int columnWidth = getColumnWidth();
		for (GraphicsPortBit bit : m_portBits) {
			Integer portWidth = bit.getPortWidth();
			int column = portWidths.indexOf(portWidth);
			int columnX = column * columnWidth; 

			Color background = gc.getBackground();
			if (bit.getPortName().equals(portName)) {
		    	gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
				gc.fillRectangle(columnX + PIN_SPACING_X, y, bit.getPixelsX(summary), bit.getPixelsY(summary));
			}
			bit.draw(display, gc, columnX + PIN_SPACING_X, y, summary);
			gc.setBackground(background);
		}
		int linkX = portWidths.size() * columnWidth;
		drawLink(display, gc, linkX + PIN_SPACING_X, y, summary);
		linkX += columnWidth;
		drawPin(display, gc, linkX + PIN_SPACING_X, y, summary);
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
	public String getShortName() {
		return getName();
	}

	@Override
	public String getToolTip() {
		return m_name;
	}

	public String getPinName() {
		return m_name;
	}

	public void addXLink(GraphicsLink link) {
		assert(m_link == null);
		m_link = link;
		addChild(link);
	}

	public void addPortBit(GraphicsPortBit port) {
		m_portBits.add(port);
		addChild(port);

		m_portWidths.add(port.getPortWidth());
		Collections.sort(m_portWidths);
		Collections.reverse(m_portWidths);
	}

	public List<Integer> getPortWidths() {
		return m_portWidths;
	}

	private void drawRow(Display display, GC gc, int pinNumber, int y, int width, int rowHeight) {
		if ((pinNumber % 2) == 1) {
			gc.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		} else {
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		}
		gc.fillRectangle(0, y, width, rowHeight);
	}

	private void drawPin(Display display, GC gc, int x, int y, boolean summary) {
		track(x, y);
		Image image = Icons.getIcon(getIconName());
		if (image != null) {
			gc.drawImage(image, x, y);
		}
		
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawText(m_name, x + getImagePixelsX() + PIN_SPACING_X, y);
    }

	private void drawLink(Display display, GC gc, int x, int y, boolean summary) {
		if (m_link != null) {
			m_link.draw(display, gc, x, y, summary);
		}
	}

	private int getColumnWidth() {
		return getImagePixelsX() + SHORT_NAME_CHARS * GraphicsUtils.getAverageCharWidth() + PIN_SPACING_X;
	}
}
