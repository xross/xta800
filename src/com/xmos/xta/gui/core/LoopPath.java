package com.xmos.xta.gui.core;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.gui.XtaGui;

public class LoopPath implements RouteNode {
    private Block m_loopPath;
    public LoopPath(Block loopPath, Block caller, Engine engine){
        m_loopPath = loopPath;
    }
    
    public Block getLoopPath(){
        return m_loopPath;
    }
    
    public String getToolTipText(){
        String toolTipText = "";
        toolTipText += "<HTML>" + getSummary() + "<br>";
        toolTipText += "<HTML>Total time taken: " + getTimeTaken() + " ns<br>";
        toolTipText += "<HTML>Total number of cycles: " + getNumCycles() + "<br>";
        toolTipText += "<HTML>Total number of FNOPs: " + getNumFNops() + "<br>";
        toolTipText += "<HTML>Total number of pausable: " + getNumPausable() + "<br>";
        toolTipText +=" <HTML>";
        return toolTipText;
    }

    public String getDescription() {
        return getTimeTaken() + " ns - " + getSummary(); 
    }

    private String getSummary() {
        String description = "Loop: " + m_loopPath.getLoopPaths().size() + " path";
        if (m_loopPath.getLoopPaths().size() > 1) {
            description += "s";
        }
        
        int numIterations = getNumIterations();
        description += ", " + numIterations + " iteration";
        if (numIterations > 1) {
            description += "s";
        }
        return description;
    }

    public int getNumIterations() {
        int numIterations = 0;
        for (Block loopPath : m_loopPath.getLoopPaths()) {
            numIterations += m_loopPath.getLoopPathAttributes(loopPath).getNumIterations();
        }
        return numIterations;
    }

    public ImageIcon getIcon() {
        try {
            return new ImageIcon(XtaGui.class
                    .getResource("Loop.png"));
        } catch (Exception e) {
            System.err.println("Warning: Could not find icon image");
        }
        return new ImageIcon();
    }

    public int getId() {
        return m_loopPath.getId();
    }

    public int getInstanceId() {
        return m_loopPath.getInstanceId();
    }

    public int getNumCycles() {
        int numCycles = 0;
        for (Block loopPath : m_loopPath.getLoopPaths()) {
            int numIterations = m_loopPath.getLoopPathAttributes(loopPath).getNumIterations();
            numCycles += loopPath.getNumCycles(true) * numIterations;
        }
        return numCycles;
    }
    
    public int getNumFNops() {
        int numFNops = 0;
        for (Block loopPath : m_loopPath.getLoopPaths()) {
            int numIterations = m_loopPath.getLoopPathAttributes(loopPath).getNumIterations();
            numFNops += loopPath.getNumFNops(true) * numIterations;
        }
        return numFNops;
    }

    public int getNumPausable() {
        int numPausable = 0;
        for (Block loopPath : m_loopPath.getLoopPaths()) {
            int numIterations = m_loopPath.getLoopPathAttributes(loopPath).getNumIterations();
            numPausable += loopPath.getNumPausables(true) * numIterations;
        }
        return numPausable;
    }

    public double getTimeTaken() {
        double timeTaken = 0.0;
        for (Block loopPath : m_loopPath.getLoopPaths()) {
            int numIterations = m_loopPath.getLoopPathAttributes(loopPath).getNumIterations();
            timeTaken += loopPath.getTimeTaken(true) * numIterations;
        }
        return timeTaken;
    }

    public int getBranchCount() {
        int count = 0;
        for(Block block : m_loopPath.getPathBlocks()){
            count += block.getLoopPaths().size();
        }
        return count;
    }

    public ArrayList<RouteNode> getBranchBlocks() {
        return null;
    }

    public Color getColour() {
        return Color.orange.brighter();
    }

    public Shape getShape(double width, Point startPoint, Point center, double startAngle, double endAngle, double radius) {
        return new Ellipse2D.Double(startPoint.getX()- width/2, startPoint.getY()- width/2, width, width);
    }

    public Shape getCrumShape(double radius, Point startPoint) {
        return new Ellipse2D.Double(startPoint.getX()- radius/2, startPoint.getY()- radius/2, radius, radius);
    }

    public JPopupMenu getPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        
        return popup;
    }
    public void getAllBlocks(TreeSet<Block> blocks) {
        for (Block loopPath : m_loopPath.getLoopPaths()) {
            loopPath.getAllBlocks(blocks);
        }
    }

    public Block getBlock() {
        return m_loopPath;
    }

    public Vector<XtaInstruction> getInstructions() {
        Vector<XtaInstruction> insts = new Vector<XtaInstruction>();
        for (Block loopPath : m_loopPath.getLoopPaths()) {
            insts.addAll(loopPath.getInstructions(true));
        }
        return insts;
    }

    public String getTypeName() {
        return "loop";
    }
}
