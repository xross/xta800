package com.xmos.xta.swing.core;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;

public interface RouteNode {
    public double               getTimeTaken();
    public int                  getNumCycles();
    public int                  getId();
    public int                  getInstanceId();
    
    public Vector<XtaInstruction> getInstructions();
 
    public String               getToolTipText();
    public String               getDescription();
    
    public ImageIcon            getIcon();
    
    public Block                getBlock();
    
    public Color                getColour();
    public Shape                getShape(double width, Point startPoint, Point center, double startAngle, double endAngle, double radius);
    public Shape                getCrumShape(double radius, Point startPoint);
    
    public JPopupMenu           getPopupMenu();
    public void                 getAllBlocks(TreeSet<Block> blocks);
    public String               getTypeName();
    
}
