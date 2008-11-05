package com.xmos.xta.gui.core;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.gui.XtaGui;

public class RootPath implements RouteNode {

    private Block  m_rootPath;
    private Block  m_caller;
    private Engine m_engine;
    private JFrame m_mainFrame;
    private XtaGui m_xtaGui;

    public RootPath(Block rootPath, Block caller, int depth, Engine engine, JFrame mainFrame, XtaGui xtaGui){
        m_rootPath = rootPath;
        m_caller = caller;
        m_engine = engine;
        m_mainFrame = mainFrame;
        m_xtaGui = xtaGui;
    }

    public Block getRootPathPath() {
        return m_rootPath;
    }

    public String getDescription() {
        String description = getTimeTaken() + " ns";
        if (m_caller != null) {
            description += " - " + m_rootPath.getNumIterations() + " iterations";
        }
        description += " (path " + m_rootPath.getInstanceId() + ")";
        return description;
    }

    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "<HTML>Path Instance ID: " + m_rootPath.getInstanceId() + "<br>";
        toolTipText += "<HTML>Time taken: " + getTimeTaken() + " ns<br>";
        toolTipText += "<HTML>Number of cycles: " + getNumCycles() + "<br>";
        toolTipText += "<HTML>Number of FNOPs: " + getNumFNops() + "<br>";
        toolTipText += "<HTML>Number of pausable: " + getNumPausables() + "<br>";
        toolTipText += "<HTML>Number of unknowns: " + getNumUnknowns() + "<br>";
        toolTipText +=" <HTML>";
        return toolTipText;
    }

    public int getId() {
        return m_rootPath.getId();
    }

    public int getInstanceId() {
        return m_rootPath.getInstanceId();
    }

    public int getNumCycles() {
        startTiming();
        int num = m_rootPath.getNumCycles(true);
        endTiming();
        return num;
    }

    public double getTimeTaken() {
        startTiming();
        double timeTaken = m_rootPath.getTimeTaken(true);
        endTiming();
        return timeTaken;
    }

    public int getNumFNops() {
        startTiming();
        int num = m_rootPath.getNumFNops(true);
        endTiming();
        return num;
    }

    public int getNumPausables() {
        startTiming();
        int num = m_rootPath.getNumPausables(true);
        endTiming();
        return num;
    }

    public int getNumUnknowns() {
        startTiming();
        int num = m_rootPath.getNumUnknowns(true);
        endTiming();
        return num;
    }

    public ImageIcon getIcon() {
        // using the caller, find out if this path is absolute or not
        boolean absolute = false;
        if(m_caller != null){
            absolute = m_caller.getLoopPathAttributes(m_rootPath).isAbsolute();
        }
        
        try {
        if (absolute){
            if (m_rootPath.getNumUnknowns(true)==0){
                return new ImageIcon(XtaGui.class
                        .getResource("AbsolutePathUnknown.png"));
            } else {
                return new ImageIcon(XtaGui.class
                        .getResource("AbsolutePath.png"));
            }

        } else {
            if (m_rootPath.getNumUnknowns(true)==0){
                return new ImageIcon(XtaGui.class
                        .getResource("Path.png"));
            } else {
                return new ImageIcon(XtaGui.class
                        .getResource("PathUnknown.png"));
            }
            
        }
        } catch (Exception e) {
                System.err.println("Warning: Could not find icon image");
            }
        return new ImageIcon();
    }

    public Color getColour() {
        return new Color(174, 237, 251);
    }

    public Shape getShape(double width, Point startPoint, Point center, double startAngle, double endAngle, double m_radius) {
        Arc2D.Double outer = new Arc2D.Double();
        Arc2D inner = new Arc2D.Double();
        outer.setArcByCenter(center.getX(), center.getY(), m_radius+2, startAngle+0.5, (endAngle-startAngle)-0.5, Arc2D.PIE);
        inner.setArcByCenter(center.getX(), center.getY(), m_radius-width, (startAngle-2), (endAngle-startAngle+4), Arc2D.PIE);
        Area arc = new Area(outer);
        arc.subtract(new Area(inner));
        AffineTransform at = new AffineTransform ();
        at.setToScale(-1, 1);
        arc = arc.createTransformedArea(at);
        at.setToTranslation(center.getX()*2, 0);
        arc = arc.createTransformedArea(at);
        return arc;
    }

    public Shape getCrumShape(double radius, Point startPoint) {
        return new Rectangle2D.Double(startPoint.getX() - radius/2, startPoint.getY()- radius/2, radius, radius);
    }
    
    public JPopupMenu getPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem setIterations = new JMenuItem("Set iteration properties");
        setIterations.addMouseListener(new MouseAdapter(){
            
            public void mousePressed(MouseEvent e) {

                int count = m_caller.getLoopPathAttributes(m_rootPath).getNumIterations();
                
                final JDialog id = new JDialog();
                id.setLocationRelativeTo(m_mainFrame.getContentPane());
                id.setTitle("Set Iteration Properties");
                id.setVisible(true);
                
                final JCheckBox scopeButton = new JCheckBox("Apply to all instances.");
                final JCheckBox absoluteButton = new JCheckBox("Make count absolute.");
                
                absoluteButton.setSelected(m_caller.getLoopPathAttributes(m_rootPath).isAbsolute());
 
                final JSpinner spinner = new JSpinner();
                SpinnerModel model = new SpinnerNumberModel(count,0,Integer.MAX_VALUE,1); 
                spinner.setModel(model);
                
                JButton button = new JButton("Set");
                button.addMouseListener(new MouseAdapter(){

                    public void mousePressed(MouseEvent e) {
                        m_engine.setLoopIterations(m_rootPath.getId(), 
                                scopeButton.isSelected(), 
                                absoluteButton.isSelected(), 
                                (Integer)(spinner.getValue()));
                        id.dispose();
                    }
                    
                });

                id.setLayout(new GridLayout(4,1));
                id.add(scopeButton);
                id.add(absoluteButton);
                id.add(spinner);
                id.add(button);
                
                id.pack();
            }
        });
        
        if (m_caller != null) popup.add(setIterations);
        JMenuItem analysePath = new JMenuItem("Analyse just this");
        analysePath.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                String startPc = Integer.toHexString(m_rootPath.getInstructions(false).get(0).getPc());
                String endPc = Integer.toHexString(m_rootPath.getInstructions(false).lastElement().getPc());
                m_engine.analyseRoute(startPc, endPc, false);
            }
        });
        popup.add(analysePath);
        
        JMenuItem traceToConsole = new JMenuItem("Trace in console");
        traceToConsole.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
            	m_xtaGui.sendToConsole(String.format("trace %d", m_rootPath.getInstanceId()));
            }
        });
        popup.add(traceToConsole);
        return popup;
    }

    public void getAllBlocks(TreeSet<Block> blocks) {
        m_rootPath.getAllBlocks(blocks);
    }

    public Block getBlock() {
        return m_rootPath;
    }

    public Vector<XtaInstruction> getInstructions() {
        startTiming();
        Vector<XtaInstruction> instructions = m_rootPath.getInstructions(true);
        endTiming();
        return instructions;
    }

    public String getTypeName() {
        return "path";
    }

    private void startTiming() {
        if (m_caller != null) {
            m_caller.incrementSeen();
        }
    }

    private void endTiming() {
        if (m_caller != null) {
            m_caller.decrementSeen();
        }
    }
    
}
