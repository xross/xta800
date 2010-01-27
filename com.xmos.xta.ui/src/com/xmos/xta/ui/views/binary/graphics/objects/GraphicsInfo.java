package com.xmos.xta.ui.views.binary.graphics.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.ui.views.binary.graphics.GraphicsUtils;

public class GraphicsInfo extends GraphicsObject {

	private final int BORDER_X = 5;
	private final int BORDER_Y = 5;

	private final int SIZE_X = 100;
	private final int SIZE_Y = 100;

	private String m_name;
	private List<String> m_labels;
	private Map<String, Integer> m_values;
	private Map<String, Color> m_colors;
	
    public GraphicsInfo(String name) {
    	m_name = name;
    	m_labels = new LinkedList<String>();
    	m_values = new TreeMap<String, Integer>();
    	m_colors = new TreeMap<String, Color>();
    }

	public void addValue(String label, int value, Color color) {
		Integer existingValue = m_values.get(label);
		
		if (existingValue == null) {
			m_labels.add(label);
			m_values.put(label, value);
			m_colors.put(label, color);
			
		} else {
			m_values.put(label, value + existingValue);
		}
    }
    
	@Override
	public void draw(Display display, GC gc, int x, int y, double scale, boolean summary) {
		track(x, y);

		int textHeight = gc.getFontMetrics().getHeight() + 1;
    	drawShape(display, gc, x, y, textHeight);

    	Rectangle charArea = new Rectangle(x + BORDER_X, y + textHeight + BORDER_Y, 
    			SIZE_X, SIZE_Y - textHeight);
    	GraphicsUtils.drawChart(display, gc, charArea, m_labels, m_colors, m_values);
    }

	private void drawShape(Display display, GC gc, int x, int y, int textHeight) {
		int width = getPixelsX(false);
		int height = getPixelsY(false);
		int arcWidth = SIZE_X / 10;
		int arcHeight = height / 10;
		
		gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    	gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
    	gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
    	
		gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.drawText(m_name, x + arcWidth, y + 1);
	}

	@Override
	public String getIconName() {
		return m_name;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String getToolTip() {
		String toolTip = m_name + ":";
		
		int total = GraphicsUtils.getTotal(m_values);

		for (String label : m_labels) {
			Integer value = m_values.get(label);
			toolTip += String.format("\n%s = %d (%.2f%%)",
					label, value, ((double)value / (double)total) * 100);
		}
		return toolTip;
	}

	@Override
	public int getPixelsX(boolean summary) {
		return SIZE_X + (2 * BORDER_X);
	}

	@Override
	public int getPixelsY(boolean summary) {
		return SIZE_Y + (2 * BORDER_Y);
	}
}
