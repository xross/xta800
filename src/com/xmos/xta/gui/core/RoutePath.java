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
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class RoutePath implements RouteNode{
    private Route m_route;
    
    public RoutePath(Route route, Engine engine) {
        super();
        m_route = route;
    }

    public int getDepth() {
        return 0;
    }

    public String getDescription() {
        int numPaths = m_route.completePaths().size();
        String description = numPaths + " complete path";
        if (numPaths > 1) {
            description += "s";
        }
        return description;
    }

    public ImageIcon getIcon() {
        try {
            return new ImageIcon(BlockPath.class
                    .getResource("../Routes.png"));
        } catch (Exception e) {
            System.err.println("Warning: Could not find icon image");
        }
        return new ImageIcon();
    }

    public int getId() {
        return m_route.getId();
    }

    public int getInstanceId() {
        return m_route.getInstanceId();
    }

    public int getNumCycles() {
        return m_route.getNumCycles();
    }

    public double getTimeTaken() {
        return m_route.getTimeTaken();
    }

    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "<HTML>" + getDescription() +  "<br>";
        toolTipText +=" <HTML>";
        return toolTipText;
    }

    public int getBranchCount() {
        return m_route.completePaths().size();
    }

    public Color getColour() {
        return Color.red;
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
        // Do nothing        
    }  
    
    public Block getBlock() {
        return null;
    }
    public Route getRoute(){
        return m_route;
    }

    public Vector<XtaInstruction> getInstructions() {
        return new Vector<XtaInstruction>();
    }

    public String getTypeName() {
        return "route";
    }
}
