package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.libs.proginfoloader.ProgInfoLoader;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.ui.GuiEngine;

public abstract class GraphicsObject {
	
	private final int NUM_CHANNELS = 32;
	private final int NUM_THREADS = 8;
	private final int NUM_TIMERS = 10;
	private final int RAM_SIZE = 0x10000;
	
	private final int INFO_SPACING_X = 10;
	private final int INFO_SPACING_Y = 10;

	private List<GraphicsObject> m_children;
	protected Map<String, GraphicsInfo> m_infos;
	
    public abstract String getName();
	public abstract String getIconName();
	public abstract String getToolTip();
	public abstract void draw(Display display, GC gc, int x, int y, double scale, boolean summary);

	public abstract int getPixelsX(boolean summary);
	public abstract int getPixelsY(boolean summary);
	
	protected int m_pixelOffsetX;
	protected int m_pixelOffsetY;

    public GraphicsObject() {
    	m_children = new LinkedList<GraphicsObject>();
    	m_infos = new TreeMap<String, GraphicsInfo>();
    }

    public void addChild(GraphicsObject child) {
		m_children.add(child);
	}

	public int numChildren() {
		return m_children.size();
	}

    public List<GraphicsObject> getChildren() {
    	return m_children;
    }
    
    public void addInfo(Display display, ProgInfoEntry info) {
    	createAndAddInfo(display, info);
    }
    
	public GraphicsObject getObject(int x, int y, boolean summary) {
		if (summary) {
			GraphicsObject object = null;
			for (GraphicsObject info : m_infos.values()) {
				object = info.getObject(x, y, false);
				if (object != null) {
					return object;
				}
			}
			return null;
		}
		
		GraphicsObject object = null;
		for (GraphicsObject child : m_children) {
			object = child.getObject(x, y, summary);
			if (object != null) {
				return object;
			}
		}
		if ((x > m_pixelOffsetX) && (x < (getPixelsX(summary) + m_pixelOffsetX)) &&
			(y > m_pixelOffsetY) && (y < (getPixelsY(summary) + m_pixelOffsetY))) {
			return this;
		}
		return null;
	}

	protected void track(int x, int y) {
		m_pixelOffsetX = x;
		m_pixelOffsetY = y;
	}

	protected GraphicsInfo createAndAddInfo(Display display, ProgInfoEntry info) {
		String name = info.getName();
		if (name.equals(ProgInfoLoader.PROGINFO_STACK)) {
			name = "memory";
		}

		GraphicsInfo graphicsInfo = m_infos.get(name);
		if (graphicsInfo == null) {
			graphicsInfo = new GraphicsInfo(name);
			m_infos.put(name, graphicsInfo);
		}

		if (name.equals(ProgInfoLoader.PROGINFO_CHANNEL)) {
			addUsedFree(display, graphicsInfo, info, NUM_CHANNELS);
			
		} else if (name.equals(ProgInfoLoader.PROGINFO_THREAD)) {
			addUsedFree(display, graphicsInfo, info, NUM_THREADS);
			
		} else if (name.equals(ProgInfoLoader.PROGINFO_TIMER)) {
			addUsedFree(display, graphicsInfo, info, NUM_TIMERS);
			
		} else if (name.equals("memory")) {
			XeLoader xeLoader = GuiEngine.getEngine().getXeLoader();
			int node = info.getNode();
			int core = info.getCore();

			int program = xeLoader.getExecutableSize(node, core);
			int data = xeLoader.getDataSize(node, core);

			int worstCase = info.getWorstCase();
			int used = worstCase;
			used += program;
			used += data;
			assert(RAM_SIZE > used);
			int free = RAM_SIZE - used;

			graphicsInfo.addValue("stack", worstCase, display.getSystemColor(SWT.COLOR_CYAN));
			graphicsInfo.addValue("free", free, display.getSystemColor(SWT.COLOR_GREEN));
			graphicsInfo.addValue("data", data, display.getSystemColor(SWT.COLOR_MAGENTA));
			graphicsInfo.addValue("program", program, display.getSystemColor(SWT.COLOR_BLUE));
		}
		return graphicsInfo;
	}

	protected void drawInfo(Display display, GC gc, int x, int y, double scale, boolean summary) {
		int numElements = m_infos.size();
		
		if (numElements == 0) {
			return;
		}
		
		int numCols = numCols();
		int numRows = numRows(numCols);
		
		int offsetX = x + INFO_SPACING_X;
		int offsetY = y + INFO_SPACING_Y;
		
		int elementNum = 0;
		for (GraphicsInfo info : m_infos.values()) {
			info.draw(display, gc, offsetX, offsetY, scale, summary);

			int col = (elementNum % numCols);
			int row = (elementNum / numRows);
			if (col == (numCols() - 1)) {
				offsetX = x + INFO_SPACING_X;
				offsetY = offsetY + getRowPixelsY(row) + INFO_SPACING_Y;
			} else {
				offsetX = offsetX + getColPixelsX(col) + INFO_SPACING_X;
			}
			elementNum++;
		}
	}

	protected int getInfoPixelsX() {
		int numElements = m_infos.size();
		if (numElements == 0) {
			return 0;
		}
		
		int numCols = numCols();
		int numRows = numRows(numCols);
		int x[] = new int[numRows];
		for (int i = 0; i < numRows; i++) {
			x[i] = INFO_SPACING_X;
		}
		
		int elementNum = 0;
		for (GraphicsInfo info : m_infos.values()) {
			int rowNumber = (elementNum / numRows);
			x[rowNumber] += info.getPixelsX(false) + INFO_SPACING_X;
			elementNum++;
		}
		
		int max = 0;
		for (int i = 0; i < numRows; i++) {
			max = Math.max(max, x[i]);
		}
		return max;
	}

	protected int getInfoPixelsY() {
		int numElements = m_infos.size();
		if (numElements == 0) {
			return 0;
		}
		
		int numCols = numCols();
		int y[] = new int[numCols];
		for (int i = 0; i < numCols; i++) {
			y[i] = INFO_SPACING_Y;
		}
		
		int elementNum = 0;
		for (GraphicsInfo info : m_infos.values()) {
			int colNumber = (elementNum % numCols);
			y[colNumber] += info.getPixelsY(false) + INFO_SPACING_Y;
			elementNum++;
		}
		
		int max = 0;
		for (int i = 0; i < numCols; i++) {
			max = Math.max(max, y[i]);
		}
		return max;
	}

	private int numCols() {
		return (int) Math.ceil(Math.sqrt(m_infos.size()));
	}

	private int numRows(int numCols) {
		return m_infos.size() / numCols;
	}

	private int getColPixelsX(int col) {
		int numCols = numCols();
		assert(col < numCols);
		
		int x[] = new int[numCols];
		for (int i = 0; i < numCols; i++) {
			x[i] = 0;
		}
		
		int elementNum = 0;
		for (GraphicsInfo info : m_infos.values()) {
			int colNumber = (elementNum % numCols);
			x[colNumber] = Math.max(x[colNumber], info.getPixelsX(false));
			elementNum++;
		}
		return x[col];
	}

	private int getRowPixelsY(int row) {
		int numCols = numCols();
		int numRows = numRows(numCols);
		assert(row < numRows);
		
		int y[] = new int[numRows];
		for (int i = 0; i < numRows; i++) {
			y[i] = 0;
		}
		
		int elementNum = 0;
		for (GraphicsInfo info : m_infos.values()) {
			int rowNumber = (elementNum / numRows);
			y[rowNumber] = Math.max(y[rowNumber], info.getPixelsY(false));
			elementNum++;
		}
		
		return y[row];
	}

	private void addUsedFree(Display display, GraphicsInfo graphicsInfo, ProgInfoEntry info, int total) {
		int worstCase = info.getWorstCase();
		graphicsInfo.addValue("free", total - worstCase, display.getSystemColor(SWT.COLOR_GREEN));
		graphicsInfo.addValue("used", worstCase, display.getSystemColor(SWT.COLOR_RED));
	}
}
