package com.xmos.xta.swing.codewindow;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;

import com.xmos.xeobject.Line;

public class SideHighlighter extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private Displayer m_displayer;
    private List<Line> m_lines;
    
    public SideHighlighter (Displayer displayer, List<Line> lines) {
    	m_displayer = displayer;
        m_lines = lines;
        setPreferredSize(new Dimension(16, 30));
        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                mousePressedAction(e.getY());
            }
        });
    }
    
    //translate the mouse position into a line number
    private void mousePressedAction(int y) {
        double lineSize = (double) this.getHeight()/m_lines.size();
        int lineNumber = (int )(y / lineSize);
        m_displayer.snapToLine(lineNumber);
    }
    
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
//        double actualBarWidth = (double)this.getHeight() / m_lines.size();
//        double barWidth = actualBarWidth < 1 ? 1 : actualBarWidth;
        g2.setColor(this.getBackground());
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
//        for(Line line : m_lines){
//            if(!line.getColour().equals(Color.white)){
//                Color c = line.getColour();
//                g2.setColor(c);
//                g2.fillRect(2,(int)(actualBarWidth*m_lines.indexOf(line)), this.getWidth()-4, (int)barWidth);
//                g2.setColor(c.darker());
//                g2.drawRect(2,(int)(actualBarWidth*m_lines.indexOf(line)), this.getWidth()-4, (int)barWidth);
//            }
//        }
    }
}
