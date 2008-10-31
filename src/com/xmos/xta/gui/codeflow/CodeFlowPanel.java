package com.xmos.xta.gui.codeflow;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
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

        for(ArrayList<BasicBlock> depthList : m_basicBlockDepths){
            for(BasicBlock bb : depthList){


                for(BasicBlock nextBB : bb.getNextBasicBlocks()){
                    g2.setColor(Color.black);
                    int w = BoxHeight;
                    int x = (int) bb.getM_location().getX();
                    int y = (int) bb.getM_location().getY();
                    int X = (int) nextBB.getM_location().getX();
                    int Y = (int) nextBB.getM_location().getY();

                    Point startPoint = new Point (x, y+w/2);
                    Point endPoint = new Point (X, Y-w/2);

                    int nextDepth = getDepth(nextBB);
                    int thisDepth = m_basicBlockDepths.indexOf(depthList);



                    if(thisDepth<nextDepth){
                        g2.setColor(Color.black);
                        g2.setStroke(new BasicStroke(3));
                        double midX = startPoint.getX() + (endPoint.getX() - startPoint.getX())/2.0;
                        double midY = startPoint.getY() + (endPoint.getY() - startPoint.getY())/2.0;
                        Point midPoint = new Point((int) midX,(int) midY);
                        Point controlPoint = new Point((int)startPoint.getX(), (int)midPoint.getY());
                        QuadCurve2D.Double curve = new QuadCurve2D.Double();
                        Point[] points = {startPoint, controlPoint, midPoint};
                        curve.setCurve(points, 0);
                        g2.draw(curve);
                        controlPoint = new Point((int)endPoint.getX(), (int)midPoint.getY());
                        Point[] nextpoints = {midPoint, controlPoint, endPoint};
                        curve.setCurve(nextpoints, 0);
                        g2.draw(curve);
                    } else if(thisDepth>nextDepth){
                        g2.setColor(Color.pink);
                        g2.setStroke(new BasicStroke(3));
                        
                        double midY = startPoint.getY() + (endPoint.getY() - startPoint.getY())/2.0;
                        double midX = startPoint.getX() + (endPoint.getX() - startPoint.getX())/2.0 - (endPoint.getY() - startPoint.getY())/20;
                        
                        Point midPoint = new Point((int) midX,(int) midY);
                        
                        //Point controlPoint = new Point((int)startPoint.getX(), (int)midPoint.getY());
                        Point controlPoint = new Point((int)midPoint.getX(), (int)startPoint.getY());
                        
                        QuadCurve2D.Double curve = new QuadCurve2D.Double();
                        Point[] points = {startPoint, controlPoint, midPoint};
                        curve.setCurve(points, 0);
                        g2.draw(curve);
                        //controlPoint = new Point((int)endPoint.getX(), (int)midPoint.getY());
                        controlPoint = new Point((int)midPoint.getX(), (int)endPoint.getY());
                        Point[] nextpoints = {midPoint, controlPoint, endPoint};
                        curve.setCurve(nextpoints, 0);
                        g2.draw(curve);

                    } else {
                        if(startPoint.distance(endPoint) == 0.0){
                            //self loop
                            g2.setColor(Color.pink);
                            g2.setStroke(new BasicStroke(3));
                            g2.drawOval((int)startPoint.getX(), (int)startPoint.getY()-w-w/2, 2*w, 2*w);
                        } else {

                        }
                        
                    }
                    g2.setStroke(new BasicStroke(1));


                }
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
