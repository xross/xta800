package com.xmos.xeobjectdisplayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

@SuppressWarnings("serial")
public class XTextPane extends JTextPane {


    private Displayer       m_displayer;
    
    public XTextPane(Displayer displayer) {
        super();
        m_displayer = displayer;
        setOpaque(false);
        this.setBackground(Color.white);
    }

    public void paint(Graphics g) {  	
    	int viewWidth = (int)m_displayer.getScrollPane().getViewport()
    		.getVisibleRect().getWidth();

    	viewWidth = Math.max(viewWidth, getLongestLine());
    	viewWidth = 6000;
    	setPreferredSize(new Dimension(viewWidth, getHeight()));
    	
    	g.setColor(this.getBackground());
    	g.fillRect(0, 0, this.getWidth(), this.getHeight());
    	super.paint(g);
    }
    
    private int getLongestLine() {
    	Element section = getDocument().getDefaultRootElement();
	    int paraCount = section.getElementCount();
	    
	    int longest = 0;
	    for (int i=0; i<paraCount; i++) {
	        Element e = section.getElement(i);
	        if(e!=null){
    	        int rangeStart = e.getStartOffset();
    	        int rangeEnd = e.getEndOffset();
    	        String line = "";
    	        try {
    	            line = this.getText(rangeStart, rangeEnd-rangeStart);
    	        } catch (BadLocationException ex) {
    	        }
    	        
    	        int len = (int)getFontMetrics(m_displayer.getFont()).getStringBounds(line, this.getGraphics()).getWidth();
    	        
    	        if(len > longest)
    	        	longest = len;
    	        }
	        }
	    
	    return longest;
	}

	public boolean getScrollableTracksViewportWidth() {
        return false;
    }    
}
