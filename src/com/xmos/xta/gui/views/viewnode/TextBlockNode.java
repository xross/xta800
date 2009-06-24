package com.xmos.xta.gui.views.viewnode;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class TextBlockNode extends ViewNode {
    public static final boolean DEBUG = false;

    private int m_left;
    private int m_right;
    private int m_bottom;
    private int m_top;
    private int m_textBase;
    
    private Color m_color;
    private String m_text;
    
	public TextBlockNode(String text, Color color) {
		super(null);
		
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

	public void draw(Display display, GC gc, double scale, boolean isWorstCase) {
        if (DEBUG) System.out.printf("TextBlockNode: draw %d,%d,%d,%d,%d\n",
        		m_left, m_top, m_right, m_bottom, m_textBase);

		gc.setBackground(m_color);
    	gc.fillRectangle(m_left, m_top, m_right - m_left, m_bottom - m_top);

    	// If the brightness is too much, write the text in black
    	RGB rgb = m_color.getRGB();
    	if (((rgb.red + rgb.green + rgb.blue) > 400) || (
    			(rgb.red > 50) && (rgb.green > 50) && (rgb.blue > 50)))
    		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    	else
    		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));

        double functionNameWidth = gc.getFontMetrics().getAverageCharWidth() * m_text.length();
        if (DEBUG) { System.out.print("TextBlockNode: text "); System.out.println(functionNameWidth); }
        if (((double)m_right - m_left) > functionNameWidth){
            gc.drawString(m_text,
                    (int)(((((double)m_left + ((double)(m_right - m_left) / 2.0))) - (functionNameWidth)/2.0)),
                    m_textBase);
        }
	}

	public void annotateXy(int xOffset, int yOffset) {
		// Do nothing
	}

	public void buildClickAreas(ArrayList<ViewNode> clickAreas) {
    	clickAreas.add(this);
	}

	public boolean contains(int x, int y) {
		if ((x >= m_left) && (x <= m_right)) {
			if ((y >= m_top) && (y <= m_bottom)) {
				return true;
			}
		}
		return false;
	}

	public String getToolTipText() {
		return m_text;
	}

	public void setBounds(int x, int y, int width, int height, int textBase) {
		m_left = x;
		m_top = y;

		m_right = x + width;
		m_bottom = y + height;
		
		m_textBase = textBase;

        if (DEBUG) System.out.printf("TextBlockNode: setBounds %d,%d,%d,%d,%d\n",
        		m_left, m_top, m_right, m_bottom, m_textBase);
	}
}
