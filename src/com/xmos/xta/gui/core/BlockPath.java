package com.xmos.xta.gui.core;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.gui.XtaGui;

public class BlockPath implements RouteNode {
    private static final boolean DEBUG = XtaGui.DEBUG;

    Block m_path;

    public BlockPath(Block path) {
        m_path = path; 
    }
    
    public int getDepth() {
        return 0;
    }
    public String getDescription() {
        return getTimeTaken() + " ns";
    }
    public ImageIcon getIcon() {

        try {
            if(m_path.getUnknowns() == 0){
                return new ImageIcon(XtaGui.class
                    .getResource("BasicBlock.png"));
            } else {
                return new ImageIcon(XtaGui.class
                        .getResource("BasicBlockUnknown.png"));
            }
            
        } catch (Exception e) {
            System.err.println("Warning: Could not find icon image");
        }
        return new ImageIcon();
    }
    public int getId() {
        return m_path.getId();
    }
    public int getInstanceId() {
        return m_path.getInstanceId();
    }
    public int getNumCycles() {
        return m_path.getNumCyclesThis();
    }
    public double getTimeTaken() {
        return m_path.getTimeTakenThis();
    }
    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "<HTML>Block Instance ID: " + m_path.getInstanceId() + "<br>";
        toolTipText += "<HTML>Time taken: " + getTimeTaken() + " ns<br>";
        toolTipText += "<HTML>Number of cycles: " + getNumCycles() + "<br>";
        toolTipText += "<HTML>Number of FNOPs: " + m_path.getNumFNopsThis() + "<br>";
        toolTipText += "<HTML>Number of pausable: " + m_path.getNumPausablesThis() + "<br>";
        if(DEBUG){
            toolTipText += "<HTML>Depth: " + getDepth() + "<br>";
        }
        toolTipText +=" <HTML>";
        return toolTipText;
    }

    public Color getColour() {
        return Color.green;
    }

    public Shape getShape(double width, Point startPoint, Point center, double startAngle, double endAngle, double radius) {
        return new Ellipse2D.Double(startPoint.getX()- width/2, startPoint.getY()- width/2, width, width);
    }

    public Shape getCrumShape(double radius, Point startPoint) {
        return new Ellipse2D.Double(startPoint.getX()- radius/2, startPoint.getY()- radius/2, radius, radius);
    }

    public JPopupMenu getPopupMenu() {
        return new JPopupMenu();
    }

    public void getAllBlocks(TreeSet<Block> blocks) {
        blocks.add(m_path);
    }
    
    public Block getBlock() {
        return m_path;
    }

    public Vector <XtaInstruction> getInstructions() {
        return m_path.getInstructionsThis();
    }

    public String getTypeName() {
        return "block";
    }
}
