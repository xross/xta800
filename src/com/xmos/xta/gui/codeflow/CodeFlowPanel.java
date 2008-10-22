package com.xmos.xta.gui.codeflow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class CodeFlowPanel extends JPanel{

    private static final long serialVersionUID = 1L;
    private ArrayList<ArrayList<BasicBlock>> m_basicBlockDepths;
    
    private static final int BoxWidth = 20;
    private static final int BoxHeight = 20;
    
    
    public CodeFlowPanel (){
        
    }

    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        if(m_basicBlockDepths== null){
            return;
        }
        int height = this.getHeight();
        int width = this.getWidth();
        for(ArrayList<BasicBlock> depthList : m_basicBlockDepths){
            for(BasicBlock bb : depthList){
                Point p = new Point(
                        (int)(width*(depthList.indexOf(bb)+0.5))/depthList.size(), 
                        (int)(height*(m_basicBlockDepths.indexOf(depthList)+0.5))/m_basicBlockDepths.size());
                bb.setM_location(p);
            }
        }
        
        
        
        for(ArrayList<BasicBlock> depthList : m_basicBlockDepths){
            for(BasicBlock bb : depthList){
                for(BasicBlock nextBB : bb.getNextBasicBlocks()){
                    g2.setColor(Color.black);
                    
                    
                    //if(getDepth()){
                        
                    //}
                    Point endPoint = new Point ((int)nextBB.getM_location().getX(),
                            (int)nextBB.getM_location().getY()-BoxHeight/2);
                    
                    Point startPoint = new Point ((int)bb.getM_location().getX(),
                            (int)bb.getM_location().getY()+BoxHeight/2);
                    
                   // Point controlPoint = new Point((int)startPoint.getX(),(int) startPoint.getY()-10);

                    
                    
                   
                    //QuadCurve2D.Double curve = new QuadCurve2D.Double();
                   // Point[] points = {endPoint, controlPoint, startPoint};
                   // curve.setCurve(points, 0);
                    //g2.setColor(Color.gray);
                    g2.drawLine((int)startPoint.getX(), (int)startPoint.getY(),
                            (int)endPoint.getX(), (int)endPoint.getY());
                    g2.setColor(Color.black);
                   // g2.draw(curve);
                }
            }
        }
        
        
        for(ArrayList<BasicBlock> depthList : m_basicBlockDepths){
            for(BasicBlock bb : depthList){
                Color c;
                if(bb.getNextBasicBlocks().size() == 0){
                    c = Color.red;
                } else if(bb.getDepth() == 0){
                    c = Color.green;
                } else {
                    c = Color.orange;
                }
                g2.setColor(c);
                g2.fillRect((int)bb.getM_location().getX()-BoxWidth/2, 
                        (int)bb.getM_location().getY()-BoxHeight/2, BoxWidth, BoxHeight);
                g2.setColor(c.darker());
                g2.drawRect((int)bb.getM_location().getX()-BoxWidth/2, 
                        (int)bb.getM_location().getY()-BoxHeight/2, BoxWidth, BoxHeight);
            }
        }
        
        
        
        
        
    }
    
    private int getDepth(BasicBlock bb){
        for(ArrayList<BasicBlock> bbList : m_basicBlockDepths){
            for(BasicBlock b : bbList){
                if(b.equals(bb)) return m_basicBlockDepths.indexOf(bbList);
            }
        }
        assert(false);
        return 0;
        
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
