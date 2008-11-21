package com.xmos.xta.swing.codewindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import com.xmos.xeobject.Line;
import com.xmos.xeobject.Line.DisplayType;


public class LineHighlighter implements Highlighter.HighlightPainter {

    private Line               m_line;
    private ArrayList<Line>    m_lines;
    private JTextPane          m_textPane;
    //private double             m_timeTaken;
    public LineHighlighter(Line line){
        m_line = line;
    }


    public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
        g.setColor(m_line.getColour());
        double fontHeight = g.getFontMetrics(m_textPane.getFont()).getHeight()+3;
        int lineNumber = m_lines.indexOf(m_line);

        g.fillRect(0, 3+ (int)(lineNumber * fontHeight) , (int)m_textPane.getWidth(), (int)fontHeight);
        g.setColor(Color.black);
        String modifiers = "";
        if(m_line.getLineNumberType() == DisplayType.hex){
            g.setColor(Color.red);
            if(m_line.getFnopCount()>0){
                modifiers = "FNOP ";
            } 
        }
        if(m_line.isPausable()){
            //modifiers += m_timeTaken + "ns ";
        }
        g.drawString(modifiers, 5, 16+ (int)(lineNumber * fontHeight));
    }


    public void setLines(ArrayList<Line> lines, JTextPane textPane, Double timeTaken) {
        m_lines = lines;
        m_textPane = textPane;
        //m_timeTaken = timeTaken;
    }
    public boolean isLine(Line line){
        if(line.equals(m_line)) return true;
        else return false;
    }

}
