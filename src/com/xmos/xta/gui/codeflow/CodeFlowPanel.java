package com.xmos.xta.gui.codeflow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class CodeFlowPanel extends JPanel{

    private static final long serialVersionUID = 1L;
    private ArrayList<ArrayList<BasicBlock>> m_basicBlockDepths;
    public CodeFlowPanel (){
        
    }

    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        if(m_basicBlockDepths== null){
            return;
        }
        
        for(ArrayList<BasicBlock> depthList : m_basicBlockDepths){
            for(BasicBlock bb : depthList){
                
            }
        }
    }

    public void update(ArrayList<ArrayList<BasicBlock>> blockDepths) {
        m_basicBlockDepths = blockDepths;
        this.repaint();
    }

    public void clear() {
        m_basicBlockDepths = null;
        this.repaint();
    }

    
}
