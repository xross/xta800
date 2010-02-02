package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.libs.proginfoloader.ProgInfoLoader;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.icons.Icons;
import com.xmos.xta.ui.views.binary.graphics.GraphicsUtils;

public abstract class GraphicsObject {
	
	private final int NUM_CHANNELS = 32;
	private final int NUM_THREADS = 8;
	private final int NUM_TIMERS = 10;
	private final int RAM_SIZE = 0x10000;
	
	protected final int PIN_NAME_CHARS = 9;
	protected final int SHORT_NAME_CHARS = 7;
	protected final int PIN_SPACING_X = 5;
	protected final int PIN_SPACING_Y = 3;

	protected final int INFO_SPACING_X = 10;
	protected final int INFO_SPACING_Y = 10;

	private List<GraphicsObject> m_children;
	protected Map<String, GraphicsInfo> m_infos;
	
    public abstract String getName();
    public abstract String getShortName();
	public abstract String getIconName();
	public abstract String getToolTip();
	public abstract void draw(Display display, GC gc, int x, int y, boolean summary);

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
    
	public void getInfoNames(Set<String> names) {
		for (GraphicsObject child : m_children) {
			child.getInfoNames(names);
		}

		for (GraphicsObject info : m_infos.values()) {
			names.add(info.getName());
		}
	}

	public GraphicsObject getObject(int x, int y, boolean summary) {
		if (summary) {
			GraphicsObject object = null;
			Collection<GraphicsObject> active = getActiveInfos();
			for (GraphicsObject info : active) {
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

	protected void drawInfo(Display display, GC gc, int x, int y, boolean summary) {
		Collection<GraphicsObject> active = getActiveInfos();
		draw(display, gc, x, y, summary, active, INFO_SPACING_X, INFO_SPACING_Y);
	}
	
	protected void draw(Display display, GC gc, int x, int y, boolean summary,
			Collection<GraphicsObject> objects, int spacingX, int spacingY) {
		int numElements = objects.size();
		if (numElements == 0) {
			return;
		}
		
		int numCols = numCols(objects);
		
		int offsetX = x + spacingX;
		int offsetY = y + spacingY;
		
		int elementNum = 0;
		for (GraphicsObject object : objects) {
			object.draw(display, gc, offsetX, offsetY, summary);

			int col = (elementNum % numCols);
			int row = (elementNum / numCols);
			if (col == (numCols(objects) - 1)) {
				offsetX = x + spacingX;
				offsetY = offsetY + getRowPixelsY(objects, false, row) + spacingY;
			} else {
				offsetX = offsetX + getColPixelsX(objects, false, col) + spacingX;
			}
			elementNum++;
		}
	}

	private Collection<GraphicsObject> getActiveInfos() {
		Collection<GraphicsObject> active = new LinkedList<GraphicsObject>();
		Set<String> activeOptions = GraphicsUtils.getSelectedOptions();
		
		for (GraphicsInfo info : m_infos.values()) {
			if (activeOptions.contains(info.getName())) {
				active.add(info);
			}
		}

		return active;
	}

	protected int getPixelsX(Collection<GraphicsObject> objects, int spacingX, boolean summary) {
		int numElements = objects.size();
		if (numElements == 0) {
			return 0;
		}
		
		int numCols = numCols(objects);
		int numRows = numRows(objects, numCols);
		int x[] = new int[numRows];
		for (int i = 0; i < numRows; i++) {
			x[i] = spacingX;
		}
		
		int elementNum = 0;
		for (GraphicsObject object : objects) {
			int rowNumber = (elementNum / numCols);
			x[rowNumber] += object.getPixelsX(summary) + spacingX;
			elementNum++;
		}
		
		int max = 0;
		for (int i = 0; i < numRows; i++) {
			max = Math.max(max, x[i]);
		}
		return max;
	}

	protected int getPixelsY(Collection<GraphicsObject> objects, int spacingY, boolean summary) {
		int numElements = objects.size();
		if (numElements == 0) {
			return 0;
		}
		
		int numCols = numCols(objects);
		int y[] = new int[numCols];
		for (int i = 0; i < numCols; i++) {
			y[i] = spacingY;
		}
		
		int elementNum = 0;
		for (GraphicsObject object : objects) {
			int colNumber = (elementNum % numCols);
			y[colNumber] += object.getPixelsY(summary) + spacingY;
			elementNum++;
		}
		
		int max = 0;
		for (int i = 0; i < numCols; i++) {
			max = Math.max(max, y[i]);
		}
		return max;
	}

	protected int getImagePixelsX() {
		Image image = Icons.getIcon(getIconName());
		if (image != null) {
			return image.getBounds().width;
		}
		return 0;
	}

	protected int getImagePixelsY() {
		Image image = Icons.getIcon(getIconName());
		if (image != null) {
			return image.getBounds().height;
		}
		return 0;
	}

	protected int getInfoPixelsX() {
		Collection<GraphicsObject> active = getActiveInfos();
		return getPixelsX(active, INFO_SPACING_X, false);
	}
	
	protected int getInfoPixelsY() {
		Collection<GraphicsObject> active = getActiveInfos();
		return getPixelsY(active, INFO_SPACING_Y, false);
	}

	private int numCols(Collection<GraphicsObject> objects) {
		return (int) Math.ceil(Math.sqrt(objects.size()));
	}

	private int numRows(Collection<GraphicsObject> objects, int numCols) {
		return (objects.size() + numCols - 1) / numCols;
	}

	protected int getColPixelsX(Collection<GraphicsObject> objects, boolean summary, int col) {
		int numCols = numCols(objects);
		assert(col < numCols);
		
		int x[] = new int[numCols];
		for (int i = 0; i < numCols; i++) {
			x[i] = 0;
		}
		
		int elementNum = 0;
		for (GraphicsObject object : objects) {
			int colNumber = (elementNum % numCols);
			x[colNumber] = Math.max(x[colNumber], object.getPixelsX(summary));
			elementNum++;
		}
		return x[col];
	}

	private int getRowPixelsY(Collection<GraphicsObject> objects, boolean summary, int row) {
		int numCols = numCols(objects);
		int numRows = numRows(objects, numCols);
		assert(row < numRows);
		
		int y[] = new int[numRows];
		for (int i = 0; i < numRows; i++) {
			y[i] = 0;
		}
		
		int elementNum = 0;
		for (GraphicsObject object : objects) {
			int rowNumber = (elementNum / numCols);
			y[rowNumber] = Math.max(y[rowNumber], object.getPixelsY(summary));
			elementNum++;
		}
		
		return y[row];
	}

	private void addUsedFree(Display display, GraphicsInfo graphicsInfo, ProgInfoEntry info, int total) {
		int worstCase = info.getWorstCase();
		graphicsInfo.addValue("free", total - worstCase, display.getSystemColor(SWT.COLOR_GREEN));
		graphicsInfo.addValue("used", worstCase, display.getSystemColor(SWT.COLOR_RED));
	}

	protected int getPortWidth(String portName) {
		String widthString = portName.replaceAll(".*_([0-9]+)[A-Z]$", "$1");
		try {
			return Integer.parseInt(widthString);
		} catch (NumberFormatException e) {
			assert(false);
		}
		return 0;
	}
}
