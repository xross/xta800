package com.xmos.xta.gui.views.viewnode;
/*
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class TextBlockNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_textBase;
    private Color m_color;
    private String m_text;
    
	public TextBlockNode(String text, Color color) {
		super(null, null);
		
		m_color = color;
		m_text = text;
		m_textBase = 0;
	}
	
	public int getPixelsX() {
		return 0;
	}

	public int getPixelsY() {
		return 0;
	}

	public int getChildrenX() {
		return 0;
	}

	public int getChildrenY() {
		return 0;
	}

	public void draw(Display display, GC gc, double scale, Rectangle renderArea, boolean isWorstCase) {
		setXy(scale, renderArea);
		gc.setBackground(m_color);
    	gc.fillRectangle(m_x, m_y, m_width, m_height);

    	// If the brightness is too much, write the text in black
    	RGB rgb = m_color.getRGB();
    	if (((rgb.red + rgb.green + rgb.blue) > 400) || (
    			(rgb.red > 50) && (rgb.green > 50) && (rgb.blue > 50)))
    		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	else
    		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));

        double functionNameWidth = gc.getFontMetrics().getAverageCharWidth() * m_text.length();
        if (DEBUG) { System.out.print("TextBlockNode: text "); System.out.println(functionNameWidth); }
        if (((double)m_width) > functionNameWidth){
            gc.drawString(m_text,
                    (int)(((((double)m_x + ((double)m_width / 2.0))) - (functionNameWidth)/2.0)),
                    m_textBase);
        }
	}

	public void annotateXy(int xOffset, int yOffset) {
		// Do nothing
	}

	public String getToolTipText() {
		return m_text;
	}
}
*/