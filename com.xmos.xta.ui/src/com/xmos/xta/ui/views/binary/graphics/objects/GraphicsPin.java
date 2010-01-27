package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.ui.views.binary.graphics.GraphicsUtils;

public class GraphicsPin extends GraphicsObject {

	private static final int LABEL_SPACE_X = 20;
	private static final int PAD_X = 10;
	private static final int PAD_Y = 10;

	private static final int BORDER_X = 3;
	private static final int BORDER_Y = 3;
	
	private String m_name;
	private int m_side;
	private Image m_label;

    public GraphicsPin(Display display, int averageCharWidth, int fontHeight, int side,
    		int pinNumber, String name) {
    	int height = PAD_Y > fontHeight ? PAD_Y : fontHeight;
    	height += (2 * BORDER_Y);
    	
    	int width = PAD_X + (2 * BORDER_X) + LABEL_SPACE_X + name.length() * averageCharWidth;
		m_label = new Image(display, width, height);

		m_name = name;
		
    	assert((side == SWT.TOP) || (side == SWT.BOTTOM) || (side == SWT.LEFT) || (side == SWT.RIGHT));
    	m_side = side;

    	// Draw the pad and label
    	GC gc = new GC(m_label);
    	drawLabel(display, gc, side);
        gc.dispose();

        if ((side == SWT.TOP) || (side == SWT.BOTTOM)) {
        	ImageData data = m_label.getImageData();
        	m_label = new Image(display, GraphicsUtils.rotateLeft(data, side));
        }
    }
    
	@Override
	public int getPixelsX(boolean summary) {
		return m_label.getBounds().width;
    }
    
	@Override
	public int getPixelsY(boolean summary) {
		return m_label.getBounds().height;
    }
    
	@Override
	public void draw(Display display, GC gc, int x, int y, double scale, boolean summary) {
    	Rectangle bounds = m_label.getBounds();
    	switch (m_side) {
    	case SWT.TOP:
    	case SWT.BOTTOM:
    	case SWT.RIGHT:
    		gc.drawImage(m_label, x, y);
    		break;

    	case SWT.LEFT:
    		gc.drawImage(m_label, x - bounds.width, y);
    	break;

    	default:
    		assert(false);

    	}
    }

    private void drawLabel(Display display, GC gc, int side) {
    	Rectangle bounds = m_label.getBounds();
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));

    	switch (m_side) {
    	case SWT.TOP:
    	case SWT.RIGHT:
        	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
        	gc.fillRectangle(0, BORDER_Y, PAD_X, PAD_Y);

    		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    		gc.drawText(m_name, BORDER_X + PAD_X, BORDER_Y);
    		break;

    	case SWT.BOTTOM:
    	case SWT.LEFT:
        	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
	    	gc.fillRectangle(bounds.width - PAD_X, BORDER_Y, PAD_X, PAD_Y);
	
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			gc.drawText(m_name, BORDER_X, BORDER_Y);
    		break;

    	default:
    		assert(false);

    	}
	}

	@Override
	public String getIconName() {
		return "instruction_node";
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String getToolTip() {
		return m_name;
	}
}
