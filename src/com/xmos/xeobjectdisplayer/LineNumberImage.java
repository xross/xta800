package com.xmos.xeobjectdisplayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import com.xmos.xeobject.Line;


public class LineNumberImage implements Highlighter.HighlightPainter {

	private Line               m_line;
	private ArrayList<Line>    m_lines;
	private JTextPane          m_textPane;
	private static final int   circleWidth = 8;
	
	
	public LineNumberImage(Line line, ArrayList<Line> lines, LineHeaders lineHeaders ){
	    m_line = line;
	    m_lines = lines;
        m_textPane = lineHeaders;
	}
	
	
    public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        double fontHeight = g2.getFontMetrics(m_textPane.getFont()).getHeight()+3;
        int lineNumber = m_lines.indexOf(m_line);
        int lineTop =  (int)(lineNumber * fontHeight);

        //g2.setColor(m_line.getColour());
        //g.fillRect(0,  3+lineTop, (int)m_textPane.getWidth(), (int)fontHeight);
        if(m_line.isFoldable()){
            g2.setColor(Color.black);
            g2.drawOval(m_textPane.getWidth() - circleWidth -1 , 
                    (int)(lineTop + fontHeight/2), circleWidth, circleWidth);
            
            g2.drawLine(m_textPane.getWidth() - circleWidth +1 , (int)(lineTop + fontHeight/2 + circleWidth/2)  ,
                    m_textPane.getWidth()- 3,(int)(lineTop + fontHeight/2 + circleWidth/2)) ;
            
            if(!m_line.getShowChildern()){
                g2.drawLine(m_textPane.getWidth() - circleWidth/2 - 1, 
                        (int)(lineTop + fontHeight/2+2)  ,
                        m_textPane.getWidth() - circleWidth/2 - 1,
                        (int)(lineTop + fontHeight/2 + circleWidth)-2) ;
                
            }
        }
        
        if(m_line.isPausable()){
            g2.setColor(Color.red);
            g2.fillOval(m_textPane.getWidth() - circleWidth -1 , 
                    (int)(lineTop + fontHeight/2), circleWidth, circleWidth);
            g2.setColor(Color.red.darker());
            g2.drawOval(m_textPane.getWidth() - circleWidth -1 , 
                    (int)(lineTop + fontHeight/2), circleWidth, circleWidth);
        }
        
    }
	
}
