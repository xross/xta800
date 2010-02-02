package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.ui.icons.Icons;
import com.xmos.xta.ui.views.binary.graphics.GraphicsUtils;

public class GraphicsPortBit extends GraphicsObject {

	private String m_name;
	private String m_xnName;
	private int m_node;
	private int m_core;
	private int m_bitNum;
	private int m_portWidth;

    public GraphicsPortBit(String name, String xnName, int node, int core, int bitNum) {
    	m_name = name;
    	m_xnName = xnName;
    	m_node = node;
    	m_core = core;
    	m_bitNum = bitNum;
    	
    	m_portWidth = getPortWidth(m_name);
    }
    
    public int getNode() {
    	return m_node;
    }

    public int getCore() {
    	return m_core;
    }
    
	@Override
	public int getPixelsX(boolean summary) {
		return getImagePixelsX() + (GraphicsUtils.getAverageCharWidth() * getShortName().length());
    }
    
	@Override
	public int getPixelsY(boolean summary) {
		return Math.max(getImagePixelsY(), GraphicsUtils.getTextHeight());
    }
    
	@Override
	public void draw(Display display, GC gc, int x, int y, boolean summary) {
		track(x, y);
		Image image = Icons.getIcon(getIconName());
		if (image != null) {
			gc.drawImage(image, x, y);
		}

    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawText(getShortName(), x + getImagePixelsX(), y);
    }

	@Override
	public String getIconName() {
		return "port";
	}

	@Override
	public String getName() {
		if (m_xnName != null) {
			return String.format("%s[%d]", m_xnName, m_bitNum);
		} else {
			return String.format("%s[%d]", m_name, m_bitNum);
		}
	}

	@Override
	public String getShortName() {
		String shortName = m_name.replaceAll(".*_([0-9]+[A-Z])$", "$1");
		shortName += String.format("[%d]", m_bitNum);
		return shortName;
	}

	@Override
	public String getToolTip() {
		return getName() + String.format(" (node %d, core %d)", m_node, m_core);
	}

	public String getPortName() {
		return m_name;
	}

	public Integer getPortWidth() {
		return m_portWidth;
	}
}
