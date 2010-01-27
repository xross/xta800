package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.libs.proginfoloader.ProgInfoEntry;

public class GraphicsCore extends GraphicsObject {

	private int m_number;

    public GraphicsCore(int number) {
    	m_number = number;
    }

    public void addInfo(Display display, ProgInfoEntry info) {
    	addChild(createAndAddInfo(display, info));
    }

	@Override
	public void draw(Display display, GC gc, int x, int y, double scale, boolean summary) {
		track(x, y);
		
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
    	
    	drawInfo(display, gc, x, y, scale, summary);
    }
	
	@Override
	public String getIconName() {
		return "core";
	}

	@Override
	public String getName() {
		return String.format("Core %d", m_number);
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
		return getInfoPixelsY();
	}
}
