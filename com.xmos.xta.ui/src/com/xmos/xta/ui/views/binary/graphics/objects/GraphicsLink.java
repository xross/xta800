package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.ui.icons.Icons;
import com.xmos.xta.ui.views.binary.graphics.GraphicsUtils;

public class GraphicsLink extends GraphicsObject {

	private int m_node;
	private int m_number;
	private int m_bitNum;
	private String m_dir;

    public GraphicsLink(int node, int number, int bitNum, String dir) {
    	m_node = node;
    	m_number = number;
    	m_bitNum = bitNum;
    	m_dir = dir;
    }
    
	@Override
	public int getPixelsX(boolean summary) {
		return getImagePixelsX() + (GraphicsUtils.getAverageCharWidth() * getShortName().length());
    }
    
	@Override
	public int getPixelsY(boolean summary) {
		return getImagePixelsY();
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
		return m_dir;
	}

	@Override
	public String getName() {
		return String.format("Link%d[%d]", m_number, m_bitNum);
	}

	@Override
	public String getShortName() {
		return String.format("%d[%d]", m_number, m_bitNum);
	}

	@Override
	public String getToolTip() {
		return getName() + String.format(" (node %d)", m_node);
	}
}
