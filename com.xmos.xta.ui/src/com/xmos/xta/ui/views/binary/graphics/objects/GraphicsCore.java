package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.libs.xnloader.XnLoader;
import com.xmos.xta.ui.views.binary.graphics.GraphicsUtils;

public class GraphicsCore extends GraphicsObject {

	private String m_name;
	private int m_number;
	private Map<String, GraphicsPort> m_ports;
	private GraphicsPorts m_portsNode;

    public GraphicsCore(String name, int number) {
    	m_name = name;
    	m_number = number;
    	m_ports = new TreeMap<String, GraphicsPort>();
    	m_portsNode = new GraphicsPorts();
    }

	@Override
    public void addInfo(XnLoader xnLoader, Display display, ProgInfoEntry info) {
    	addChild(createAndAddInfo(xnLoader, display, info));
    }

	@Override
	public void draw(Display display, GC gc, int x, int y, boolean summary) {
		track(x, y);
		
		if (!summary) {
			int width = getPixelsX(summary);
			int height = getPixelsY(summary);
			int arcWidth = width / 10;
			int arcHeight = height / 10;
			
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
	    	gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	    	
	    	String label = getName();
	    	int labelX = label.length() * gc.getFontMetrics().getAverageCharWidth();
	    	if (labelX < width) {
		    	gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		    	gc.drawText(label, x + ((width - labelX) / 2), y + 1);
	    	}
	    	
	    	y += GraphicsUtils.getTextHeight();
		}
    	
    	drawInfo(display, gc, x, y, summary);
    }
	
	@Override
	public String getIconName() {
		return "core";
	}

	@Override
	public String getName() {
		if (m_name.length() > 0) {
			return m_name;
		} else {
			return String.format("Core %d", m_number);
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

	@Override
	public int getPixelsX(boolean summary) {
		return getInfoPixelsX();
	}

	@Override
	public int getPixelsY(boolean summary) {
		int y = getInfoPixelsY();
		if (!summary) {
			y += GraphicsUtils.getTextHeight();
		}
		return y;
	}

	public void addPort(String name, GraphicsPort port) {
		m_ports.put(name, port);
		if (m_portsNode.numChildren() == 0) {
			addChild(m_portsNode);
		}
		m_portsNode.addChild(port);
	}

	public GraphicsPort getPort(String name) {
		return m_ports.get(name);
	}

	public GraphicsPorts getPorts() {
		return m_portsNode;
	}
}
