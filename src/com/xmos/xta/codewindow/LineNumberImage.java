package com.xmos.xta.codewindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.Line.DisplayType;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Reference;
import com.xmos.xta.gui.XtaGui;
import com.xmos.xta.gui.XtaXeObjectDisplayer;


public class LineNumberImage implements Highlighter.HighlightPainter {

	private Line                   m_line;
	private ArrayList<Line>        m_lines;
	private JTextPane              m_textPane;
	public static final int        circleWidth = 8;
	private XtaXeObjectDisplayer   m_parent;
	private  Engine                m_engine;
	
	public LineNumberImage(Line line, ArrayList<Line> lines, LineHeaders lineHeaders, 
	        XtaXeObjectDisplayer parent, Engine engine ){
	    m_line = line;
	    m_lines = lines;
        m_textPane = lineHeaders;
        m_parent = parent;
        m_engine = engine;
	}
	
    public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        double fontHeight = g2.getFontMetrics(m_textPane.getFont()).getHeight()+3;
        int lineNumber = m_lines.indexOf(m_line);
        int lineTop =  (int)(lineNumber * fontHeight);

        if(m_line.getLineNumberType()==DisplayType.hex){
            if(m_parent.getM_startPc().equals(Integer.toHexString(m_line.getLineNumber()))){
                g2.setColor(Color.green);
                g.fillRect(0,  3+lineTop, (int)m_textPane.getWidth(), (int)fontHeight);
            } else if(m_parent.getM_endPc().equals(Integer.toHexString(m_line.getLineNumber()))){
                g2.setColor(Color.red);
                g.fillRect(0,  3+lineTop, (int)m_textPane.getWidth(), (int)fontHeight);
            }
        } else if(m_line.getLineNumberType()==DisplayType.decimal){
            if(m_parent.getM_startLine().equals(Integer.toString(m_line.getLineNumber()))){
                g2.setColor(Color.green);
                g.fillRect(0,  3+lineTop, (int)m_textPane.getWidth(), (int)fontHeight);
            } else if( m_parent.getM_endLine().equals(Integer.toString(m_line.getLineNumber()))){
                g2.setColor(Color.red);
                g.fillRect(0,  3+lineTop, (int)m_textPane.getWidth(), (int)fontHeight);
            }
        } 
        
        if(m_line.isFoldable()){
            g2.setColor(Color.black);
            g2.drawOval(m_textPane.getWidth()-1 - circleWidth -1 , 
                    (int)(lineTop + fontHeight/2), 
                    circleWidth, circleWidth);
            
            g2.drawLine(m_textPane.getWidth()-1 - circleWidth +1 ,
                    (int)(lineTop + fontHeight/2 + circleWidth/2)  ,
                    m_textPane.getWidth()-1- 3,
                    (int)(lineTop + fontHeight/2 + circleWidth/2)) ;
            
            if(!m_line.getShowChildern()){
                g2.drawLine(m_textPane.getWidth()-1 - circleWidth/2 - 1, 
                        (int)(lineTop + fontHeight/2+2)  ,
                        m_textPane.getWidth()-1 - circleWidth/2 - 1,
                        (int)(lineTop + fontHeight/2 + circleWidth)-2) ;
            }
        }
        
        boolean excluded = false;
        //this is horribly inefficient
        for(Reference ref : m_engine.getExclusions()){
            if(m_line.getLineNumberType() == DisplayType.hex ){
                if(ref.getPc() == m_line.getLineNumber()){
                    excluded = true;
                }
            }
        }
        
        if(excluded){
            if(m_line.getLineNumberType() == DisplayType.hex){
                g2.setColor(Color.gray);
                g2.fillOval(m_textPane.getWidth()-1 - circleWidth -1 , 
                        (int)(lineTop + fontHeight/2), circleWidth, circleWidth);
                g2.setColor(Color.black);
                g2.drawOval(m_textPane.getWidth()-1 - circleWidth -1 , 
                        (int)(lineTop + fontHeight/2), circleWidth, circleWidth);
            } 
            
        } else if(m_line.isPausable()){
            ImageIcon icon;
            try {
                icon = new ImageIcon(XtaGui.class.getResource("warning.png"));
            } catch (Exception e) {
                icon = new ImageIcon();
                e.printStackTrace();
            }
            icon.paintIcon(m_textPane, g2, m_textPane.getWidth()-icon.getIconWidth(), (int)(lineTop + fontHeight/2)-5);

        } 
        g2.setColor(new Color(230, 230, 230));
        g2.drawLine(m_textPane.getWidth()-1,lineTop, m_textPane.getWidth()-1,  lineTop + (int)fontHeight);
    }
	
}
