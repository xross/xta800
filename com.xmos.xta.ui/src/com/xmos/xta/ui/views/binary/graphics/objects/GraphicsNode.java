package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.xta.ui.views.binary.graphics.GraphicsUtils;

public class GraphicsNode extends GraphicsObject {
	
	private static final int CORNER_ROUNDING = 5;
	private static final int BORDER_X = 5;
	private static final int BORDER_Y = 5;

    private Map<Integer, GraphicsCore> m_cores;
	private int m_number;

	public GraphicsNode(int number) {
		m_number = number;
		m_cores = new TreeMap<Integer, GraphicsCore>();
    }

	public void addCore(int coreNumber, GraphicsCore core) {
		assert(m_cores.containsKey(coreNumber) == false);
		m_cores.put(coreNumber, core);
		addChild(core);
	}
	
	@Override
	public void addInfo(Display display, ProgInfoEntry info) {
		super.addInfo(display, info);

		int coreId = info.getCore();
		GraphicsCore core = m_cores.get(coreId);
		if (core != null) {
			core.addInfo(display, info);
		}
	}
	
	@Override
	public int getPixelsX(boolean summary) {
		if (summary) {
			return getInfoPixelsX();
		}
		
		Collection<GraphicsObject> cores = new LinkedList<GraphicsObject>(m_cores.values());
		return getPixelsX(cores, BORDER_X, summary);
	}

	@Override
	public int getPixelsY(boolean summary) {
		if (summary) {
			return getInfoPixelsY();
		}

		Collection<GraphicsObject> cores = new LinkedList<GraphicsObject>(m_cores.values());
		int y = getPixelsY(cores, BORDER_Y, summary);
		return y + GraphicsUtils.getTextHeight();
	}

	@Override
	public void draw(Display display, GC gc, int x, int y, boolean summary) {
		track(x, y);

    	if (summary) {
			drawInfo(display, gc, x, y, summary);
			return;
		}

    	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	int width = getPixelsX(summary);
    	int height = getPixelsY(summary);
    	gc.fillRoundRectangle(x, y, width, height, CORNER_ROUNDING, CORNER_ROUNDING);

    	String label = getName();
    	int labelX = label.length() * gc.getFontMetrics().getAverageCharWidth();
    	if (labelX < width) {
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
	    	gc.drawText(label, x + ((width - labelX) / 2), y + 1);
    	}
    	
		Collection<GraphicsObject> cores = new LinkedList<GraphicsObject>(m_cores.values());
    	draw(display, gc, x, y + GraphicsUtils.getTextHeight(), summary,
    			cores, BORDER_X, BORDER_Y);
    }

	@Override
	public String getIconName() {
		return "node";
	}

	@Override
	public String getName() {
		return String.format("Node %d", m_number);
	}

	@Override
	public String getShortName() {
		return getName();
	}

	@Override
	public String getToolTip() {
		return getName();
	}

	public GraphicsPort getPort(int coreNumber, String name) {
		GraphicsCore core = m_cores.get(coreNumber);
		if (core != null) {
			return core.getPort(name);
		}
		return null;
	}

	public GraphicsCore getCore(int coreNumber) {
		return m_cores.get(coreNumber);
	}
}
