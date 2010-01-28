package com.xmos.xta.ui.views.binary.graphics;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class GraphicsUtils {

	public static final String CHART_PIE = "Pie";
	public static final String CHART_BAR = "Bar";
	private static String s_chartTypes[] = { CHART_PIE, CHART_BAR };
	private static String s_selectedChartType = CHART_PIE;

	private static Set<String> s_selectedOptions = new TreeSet<String>();

	private static int s_textHeight = -1;
	private static Color s_colors[];
	
	public static ImageData rotateLeft(ImageData srcData, int direction) {
		int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		int destBytesPerLine = srcData.height * bytesPerPixel;

		byte[] newData = new byte[srcData.data.length];
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = srcY;
				int destY = srcData.width - srcX - 1;
				int destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
				int srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);

				System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
			}
		}

		int width = srcData.height;
		int height = srcData.width;
		return new ImageData(width, height, srcData.depth, srcData.palette, destBytesPerLine, newData);
	}

	public static ImageData flip(ImageData srcData, boolean vertical) {
		int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		int destBytesPerLine = srcData.width * bytesPerPixel;

		byte[] newData = new byte[srcData.data.length];
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
				if (vertical) {
					destX = srcX;
					destY = srcData.height - srcY - 1;
				} else {
					destX = srcData.width - srcX - 1;
					destY = srcY;
				}
				
				destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
				srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
				System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
			}
		}
		return new ImageData(srcData.width, srcData.height, srcData.depth, srcData.palette, destBytesPerLine, newData);
	}
	
	public static Color getColor(Display display, int i) {
		assert(s_colors != null);
		return s_colors[i % s_colors.length];
	}

	public static int getTextHeight() {
		assert(s_textHeight > 0);
		return s_textHeight;
	}

	public static void initialise(Display display) {
		List<Color> colors = new LinkedList<Color>();
		colors.add(display.getSystemColor(SWT.COLOR_GRAY));
		colors.add(display.getSystemColor(SWT.COLOR_BLUE));
		colors.add(display.getSystemColor(SWT.COLOR_GREEN));
		colors.add(display.getSystemColor(SWT.COLOR_RED));
		colors.add(display.getSystemColor(SWT.COLOR_MAGENTA));
		colors.add(display.getSystemColor(SWT.COLOR_YELLOW));
		colors.add(display.getSystemColor(SWT.COLOR_DARK_CYAN));
		s_colors = new Color[colors.size()];
		colors.toArray(s_colors);
		
		Image image = new Image(display, 1, 1);
		GC gc = new GC(image);
		s_textHeight = gc.getFontMetrics().getHeight() + 1;
		gc.dispose();
		image.dispose();
	}

	public static void drawChart(Display display, GC gc, Rectangle area,
			List<String> labels, Map<String, Color> colors, Map<String, Integer> values) {
		
		if (s_selectedChartType.equals(CHART_PIE)) {
			drawPieChart(display, gc, area, labels, colors, values);
			
		} else if (s_selectedChartType.equals(CHART_BAR)) {
			drawBarChart(display, gc, area, labels, colors, values);
			
		} else {
			assert(false);
		}
	}

	public static void setChartType(String charType) {
		for (String type : s_chartTypes) {
			if (type.equals(charType)) {
				s_selectedChartType = type;
				return;
			}
		}
		assert(false);
	}

	private static void drawBarChart(Display display, GC gc, Rectangle area,
			List<String> labels, Map<String, Color> colors, Map<String, Integer> values) {
		
		int total = getTotal(values);
		
		int infoOffsetY = area.y;
		for (String label : labels) {
			Color color = colors.get(label);
			
			int infoHeight = (area.height * values.get(label)) / total;
			int infoWidth = area.width / 2;
			int infoOffsetX = area.width / 4;

			gc.setBackground(color);
	    	gc.setForeground(color);
	    	gc.fillRectangle(area.x + infoOffsetX, infoOffsetY, infoWidth, infoHeight);
	    	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.drawRectangle(area.x + infoOffsetX, infoOffsetY, infoWidth, infoHeight);
	    	
	    	infoOffsetY += infoHeight;
		}
	}

	private static void drawPieChart(Display display, GC gc, Rectangle area, 
			List<String> labels, Map<String, Color> colors, Map<String, Integer> values) {

		double total = (double)getTotal(values);
		double curValue = 0;
		int startAngle = 0;
		int i = 0;
		for (String label : labels) {
			startAngle = (int)(curValue * 360 / total);
			double value = (double)values.get(label);
			int arcAngle = (int)(value * 360 / total);

			if (i == labels.size() - 1) {
				arcAngle = 360 - startAngle;
			}

			Color color = colors.get(label);
			gc.setBackground(color);
	    	gc.setForeground(color);
			gc.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);

	    	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.drawArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);
			curValue += value;
			i++;
		}
	}
	
	public static int getTotal(Map<String, Integer> values) {
		int total = 0;
		for (Integer value : values.values()) {
			total += value;
		}
		return total;
	}
	
	public static void setSelectedOptions(Set<String> options) {
		s_selectedOptions = options;
	}

	public static Set<String> getSelectedOptions() {
		return s_selectedOptions;
	}
}
