package com.xmos.xta.ui.views.binary.graphics.objects;

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
		
		int numCores = numCores();
		if (numCores == 4) {
			int x0 = m_cores.get(0).getPixelsX(summary);
			int x1 = m_cores.get(1).getPixelsX(summary);
			int x2 = m_cores.get(2).getPixelsX(summary);
			int x3 = m_cores.get(3).getPixelsX(summary);
			return (Math.max(x0, x2) + Math.max(x1, x3)) + BORDER_X * 3;

		} else {
			return m_cores.get(0).getPixelsX(summary) + BORDER_X * 2;
		}
	}

	@Override
	public int getPixelsY(boolean summary) {
		if (summary) {
			return getInfoPixelsY();
		}

		int y = 0;
		int numCores = numCores();
		if (numCores == 4) {
			int y0 = m_cores.get(0).getPixelsY(summary);
			int y1 = m_cores.get(1).getPixelsY(summary);
			int y2 = m_cores.get(2).getPixelsY(summary);
			int y3 = m_cores.get(3).getPixelsY(summary);
			y = (Math.max(y0, y1) + Math.max(y2, y3)) + BORDER_Y * 3;

		} else {
			y = m_cores.get(0).getPixelsY(summary) + BORDER_Y * 2;
		}
		return y + GraphicsUtils.getTextHeight();
	}

	@Override
	public void draw(Display display, GC gc, int x, int y, double scale, boolean summary) {
		track(x, y);

    	if (summary) {
			drawInfo(display, gc, x, y, scale, summary);
			return;
		}

    	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	int width = getPixelsX(summary);
    	int height = getPixelsY(summary);
    	gc.fillRoundRectangle(x, y, width, height, CORNER_ROUNDING, CORNER_ROUNDING);

    	String label = String.format("Node %d", m_number);
    	int labelX = label.length() * gc.getFontMetrics().getAverageCharWidth();
    	if (labelX < width) {
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
	    	gc.drawText(label, x + ((width - labelX) / 2), y + 1);
    	}
    	
    	int column0X = x + BORDER_X;
    	int row0Y = y + BORDER_Y + GraphicsUtils.getTextHeight();
    	
		int numCores = m_cores.size();
		if (numCores == 4) {
			int x0 = m_cores.get(0).getPixelsX(summary);
			int x2 = m_cores.get(2).getPixelsX(summary);
			int column1X = column0X + BORDER_X + Math.max(x0, x2);

			int y0 = m_cores.get(0).getPixelsY(summary);
			int y1 = m_cores.get(1).getPixelsY(summary);
			int row1Y = row0Y + BORDER_Y + Math.max(y0, y1);

			m_cores.get(0).draw(display, gc, column0X, row0Y, scale, summary);
			m_cores.get(1).draw(display, gc, column1X, row0Y, scale, summary);
			m_cores.get(2).draw(display, gc, column0X, row1Y, scale, summary);
			m_cores.get(3).draw(display, gc, column1X, row1Y, scale, summary);
		} else if (numCores == 1) {
			m_cores.get(0).draw(display, gc, column0X, row0Y, scale, summary);
		}
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
	public String getToolTip() {
		return getName();
	}

	private int numCores() {
		int numCores = m_cores.size();
		assert(numCores == 4 || numCores == 1);
		return numCores;
	}
}
