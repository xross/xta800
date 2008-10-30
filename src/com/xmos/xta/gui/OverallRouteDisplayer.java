package com.xmos.xta.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.xmos.elfhandler.Symbol;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.gui.core.BlockPath;
import com.xmos.xta.gui.core.LoopPath;
import com.xmos.xta.gui.core.PathStatus;
import com.xmos.xta.gui.core.RouteNode;

public class OverallRouteDisplayer implements PathUpdater {

    private JPanel                  m_overallRouteDisplayerPanel;
    private JPanel                  m_functionPanel;
    private JPanel                  m_functionTimeLine;
    private JPanel                  m_depthTimeLine;
    private JPanel                  m_depthPanel;
    private PathStatus              m_pathStatus;
    private TreePath                m_routeSelection;
    private Vector<XtaCodeBlock>    m_blockList;
    private DefaultMutableTreeNode  m_selectedNode;
    
    private SymbolTable             m_table;

    private int m_colorIndex;
    private Vector<Color> m_colors;
    private HashMap<String, Color> m_functionColors;
    
    private int m_pathDepth;
    private int m_pathLength;
    private int m_blockLength;
    private int m_blockDepth;
    private int m_blockPausable;
    private int m_blockFNops;
    private boolean showTime = false;
    private ArrayList<Box> m_boxes;
    private Engine m_engine;
    
    public OverallRouteDisplayer(PathStatus pathStatus, Engine engine) {
        initaliseColors();
        m_engine = engine;
        m_functionColors = new HashMap<String, Color>();
        
        m_functionTimeLine = new JPanel(){
            private static final long serialVersionUID = 1L;
            
            public void paint(Graphics g){
                Graphics2D g2 = (Graphics2D)g;
                drawTimeLine(g2);
            }
        };
        
        m_functionTimeLine.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == 3){
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem scaleView = new JMenuItem();
                    if(showTime)scaleView.setText("Set scale to thread cycles");
                    else scaleView.setText("Set scale to time elapsed");
                    scaleView.addMouseListener(new MouseAdapter(){
                        public void mousePressed(MouseEvent e) {
                            showTime=!showTime;
                            m_functionTimeLine.repaint();
                        }
                    });
                    popup.add(scaleView);
                    popup.setOpaque(true);
                    popup.setLightWeightPopupEnabled(true);
                    popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
                }
                
            }
        });
        m_functionTimeLine.setPreferredSize(new Dimension(32, 32));
        m_depthTimeLine = new JPanel(){
            private static final long serialVersionUID = 1L;
            
            public void paint(Graphics g){
                Graphics2D g2 = (Graphics2D)g;
                drawTimeLine(g2);
            }
        };
        
        m_depthTimeLine.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == 3){
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem scaleView = new JMenuItem();
                    if(showTime)scaleView.setText("Set scale to thread cycles");
                    else scaleView.setText("Set scale to time elapsed");
                    scaleView.addMouseListener(new MouseAdapter(){
                        public void mousePressed(MouseEvent e) {
                            showTime=!showTime;
                            m_depthTimeLine.repaint();
                        }
                    });
                    popup.add(scaleView);
                    popup.setOpaque(true);
                    popup.setLightWeightPopupEnabled(true);
                    popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
                }
                
            }
        });
        m_depthTimeLine.setPreferredSize(new Dimension(32, 32));
        
        m_boxes = new ArrayList<Box>();
        
        m_blockList = new Vector<XtaCodeBlock>();

        this.m_pathStatus = pathStatus;

        JTabbedPane tabbedPane = new JTabbedPane();

        m_depthPanel = new JPanel(){
            private static final long serialVersionUID = 1L;
            public void paint(Graphics g){
                drawDepthPanel(g);
            }
        };
        
        m_functionPanel = new JPanel(){
            private static final long serialVersionUID = 1L;
            public void paint(Graphics g){
                drawFunctionPanel(g);
            }
        };
        
        m_functionPanel.addMouseMotionListener(new MouseOver(m_boxes, m_functionPanel));
        m_depthPanel.addMouseMotionListener(new MouseOver(m_boxes, m_depthPanel));
        
        tabbedPane.addTab("Function View", m_functionPanel);
        tabbedPane.addTab("Loop View", m_depthPanel);

    }
    
    public void displayRoute(Route route) {
        m_blockList.clear();
    }
    
    private String m_currentFunction;
    private String m_previousFunction;
    private int m_currentDepth;
    
    private boolean addPath(DefaultMutableTreeNode node, int numIterations,
            boolean previousBlockSelected, boolean currentBlockSelected) {

        if (numIterations == 0)
            return previousBlockSelected;
        
        // Set the current selected 
        if (node == m_selectedNode) {
            currentBlockSelected = true;
        }

        if (node.getUserObject() instanceof BlockPath) {
            BlockPath block = (BlockPath)node.getUserObject();

            for (XtaInstruction inst : block.getInstructions()) {
                String functionName = m_table.functionName(inst.getPc());
    
                if (m_blockLength == 0) {
                    previousBlockSelected = currentBlockSelected;
                }
                    
                if (m_currentFunction.length() == 0) {
                    m_currentFunction = functionName;
                    m_previousFunction = functionName;
                }
    
                if ((m_currentFunction.equals(functionName) == false) || 
                    (m_currentDepth != m_blockDepth) ||
                    (currentBlockSelected != previousBlockSelected)) {
                    
                    if (m_blockLength > 0) {
                        addBlock(previousBlockSelected);
                        m_currentFunction = functionName;
                        previousBlockSelected = currentBlockSelected;
                    } else {
                        m_currentFunction = m_previousFunction;
                    }
                }
                m_blockLength += numIterations;
                m_blockDepth = m_currentDepth;
                
                if (inst.canPause())
                    m_blockPausable++;
    
                if (inst.isFnop())
                    m_blockFNops++;
            }
        }

        boolean subBlockSelected = currentBlockSelected;
        for (int child = 0; child < node.getChildCount(); child++) {
            DefaultMutableTreeNode subNode = (DefaultMutableTreeNode)node.getChildAt(child);

            RouteNode routeNode = (RouteNode)((DefaultMutableTreeNode)(subNode)).getUserObject();
            numIterations = routeNode.getBlock().getNumIterations();

            if (routeNode instanceof LoopPath) {
                m_currentDepth++;
            }
            subBlockSelected = addPath(subNode, numIterations, subBlockSelected, currentBlockSelected);
            if (routeNode instanceof LoopPath) {
                m_currentDepth--;
            }
        }
        
        if (m_blockLength > 0) {
            addBlock(currentBlockSelected);
        }
        return currentBlockSelected;
    }

    private void addBlock(boolean blockSelected) {
        m_blockList.add(new XtaCodeBlock(m_currentFunction, m_blockLength,
                m_blockDepth, m_blockPausable,
                m_blockFNops, blockSelected));
        m_pathLength += m_blockLength;
        m_blockLength = 0;
        m_blockPausable = 0;
        m_blockFNops = 0;
        if (m_currentDepth > m_pathDepth)
            m_pathDepth = m_currentDepth;
        m_previousFunction = m_currentFunction;
    }

    private void drawDepthPanel(Graphics g) {
        int totalWidth = m_depthPanel.getWidth();
        int totalHeight = m_depthPanel.getHeight();

        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        g2.fillRect(0, 0, totalWidth, totalHeight);
        
        if (m_blockList.isEmpty())
            return;

        int borderY = 10;
        double blockHeight = (((double)totalHeight - (2.0 * (double)borderY)) / ((double)m_pathDepth/2.0 + 1));
        double instWidth = (double)(totalWidth - 2) / (double)m_pathLength;

        int x = 0;
        boolean wasSelected = true;
        for (int i = 0; i < m_blockList.size(); i++) {
            XtaCodeBlock block = m_blockList.get(i);
            boolean selected = block.getSelected();
            
            boolean drawLeft = (!selected && wasSelected);
            boolean drawRight = (!selected &&
                    ((i == (m_blockList.size() - 1)) || m_blockList.get(i+1).getSelected()));
            wasSelected = selected;
            
            int y = borderY + (int)((m_pathDepth - block.getDepth()) * (blockHeight / 2.0));
            x += drawFunctionBlock(g2, block, x, y, (int)blockHeight, totalHeight,
                    instWidth, false, drawLeft, drawRight);
        }
    }
    
    private void drawFunctionPanel(Graphics g){
        int totalWidth = m_functionPanel.getWidth();
        int totalHeight = m_functionPanel.getHeight();

        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        g2.fillRect(0, 0, totalWidth, totalHeight);
        
        if (m_blockList.isEmpty())
            return;

        int borderY = totalHeight / 6;
        int y = borderY;
        int blockHeight = totalHeight - (2 * borderY);
        double instWidth = (double)(totalWidth - 2) / (double)m_pathLength;

        int x = 0;
        boolean wasSelected = true;
        for (int i = 0; i < m_blockList.size(); i++) {
            XtaCodeBlock block = m_blockList.get(i);
            boolean selected = block.getSelected();
            
            boolean drawLeft = (!selected && wasSelected);
            boolean drawRight = (!selected &&
                    ((i == (m_blockList.size() - 1)) || m_blockList.get(i+1).getSelected()));
            wasSelected = selected;
            
            x += drawFunctionBlock(g2, block, x, y, blockHeight, totalHeight,
                    instWidth, true, drawLeft, drawRight);
        }
        
    }
    
    private int drawFunctionBlock(Graphics2D g2, XtaCodeBlock block,
            int x, int y, int height, int totalHeight,
            double instWidth, boolean drawDetails,
            boolean drawLeft, boolean drawRight) {
        
        Color color = m_functionColors.get(block.getFunction());

        g2.setColor(color);

        int width = (int)(Math.ceil(instWidth * block.getLength()));
        g2.fillRect(x, y, width, height);
        g2.setColor(color.darker());
        g2.drawRect(x, y, width, height);
        
        m_boxes.add(new Box(x, y, width, height, block));
        
        if (drawDetails && (block.getPausable() > 0)) {
            // Show the pausable
            int fnopWidth = (int)Math.ceil(instWidth * block.getPausable()) - 1;
            g2.setColor(color.brighter());
            g2.fillRect(x, y + (height / 3), fnopWidth, (height / 3) - 1);
            g2.setColor(Color.black);
            g2.drawRect(x, y + (height / 3), fnopWidth, (height / 3) - 1);
        }

        if (drawDetails && (block.getFNops() > 0)) {
            // Show the FNops
            int pausableWidth = (int)Math.ceil(instWidth * block.getFNops()) - 1;
            g2.setColor(color.brighter().brighter());
            g2.fillRect(x, y + ((2 * height) / 3), pausableWidth, (height / 3) - 1);
            g2.setColor(Color.black);
            g2.drawRect(x, y + ((2 * height) / 3), pausableWidth, (height / 3) - 1);
        }
        
        g2.setColor(Color.white);
        String functionName = block.getFunction();
        double functionNameWidth = g2.getFontMetrics().getStringBounds(functionName, g2).getWidth();
        double functionNameHeight = g2.getFontMetrics().getStringBounds(functionName, g2).getHeight();

        if (width > (int)functionNameWidth){
            g2.drawString(functionName,
                    (int)(x + width/2.0 - functionNameWidth/2.0),
                    (int)(y + height/2.0 + functionNameHeight/2.0));
        }

        if (!block.getSelected()) {
            Composite c = g2.getComposite();
            float alpha = .5f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.gray.brighter());
            g2.fillRect(x, 0, width, totalHeight);
            g2.setComposite(c);
        }
        if (drawLeft) {
            g2.setColor(Color.black);
            g2.drawLine(x, 0, x, totalHeight);
        }
        if (drawRight) {
            g2.setColor(Color.black);
            g2.drawLine(x + width, 0, x + width, totalHeight);
        }
        return width;
    }

    public JPanel getPanel(){
        return m_overallRouteDisplayerPanel;
    }
    
    public JPanel getFunctionPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(m_functionPanel, BorderLayout.CENTER);
        panel.add(m_functionTimeLine, BorderLayout.SOUTH);
        return panel;
    }
    public JPanel getDepthPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(m_depthPanel, BorderLayout.CENTER);
        panel.add(m_depthTimeLine, BorderLayout.SOUTH);
        return panel;
    }

    public void notifyUpdateState() {
        
        if ((m_routeSelection != null) && 
            (m_routeSelection.getPath().length) > 0) 
        {
            Object [] path =  m_routeSelection.getPath();
            int pathLength = m_routeSelection.getPath().length;
            
            if ((pathLength > 1) &&
                ((DefaultMutableTreeNode)(path[1])).getUserObject() instanceof RouteNode)
            {
                DefaultMutableTreeNode userPath = (DefaultMutableTreeNode)path[1];
                m_selectedNode = (DefaultMutableTreeNode)path[pathLength - 1];

                clearAll();
                addPath(userPath, 1, false, false);
            } else if (m_routeSelection.getPath().length<2){
                clearAll();
            }

        }
        m_functionPanel.repaint();
        m_depthPanel.repaint();
        m_functionTimeLine.repaint();
        m_depthTimeLine.repaint();
    }

    private void getDebugInfo(){
        m_table = m_pathStatus.getSymbolTable();
        Iterator<Map.Entry<Integer, Symbol>> iter =
            m_table.getElfHandler().getFunctionsPcKeyed().entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry<Integer, Symbol> pair = iter.next();
            Symbol symbol = pair.getValue();  
            if (!m_functionColors.containsKey(symbol.getName())){
                m_functionColors.put(symbol.getName(), getColor());
            }
        }
        
    }
    
    private void clearAll() {
        m_boxes.clear();
        m_blockList.clear();
        m_pathDepth = 0;
        m_pathLength = 0;
        m_blockLength = 0;
        m_blockDepth = 0;
        m_blockPausable = 0;
        m_blockFNops = 0;
        
        m_currentFunction = "";
        m_previousFunction = "";
        m_currentDepth = 0;
    }
    
    public void displayRoutewithDebugInfo(Route route, DebugInfo debugInfo,
            MemoryImage memoryImage, SymbolTable table) {
        m_table = table;
         Iterator<Map.Entry<Integer, Symbol>> iter =
             m_table.getElfHandler().getFunctionsPcKeyed().entrySet().iterator();
         
         while (iter.hasNext()) {
             Map.Entry<Integer, Symbol> pair = iter.next();
             Symbol symbol = pair.getValue();  
             if (!m_functionColors.containsKey(symbol.getName())){
                 m_functionColors.put(symbol.getName(), getColor());
             }
         }
    }
    
    private Color getColor() {
        if (m_colorIndex < m_colors.size()) {
            Color color = m_colors.get(m_colorIndex);
            m_colorIndex++;
            return color;
        }
        return new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
    }

    private void initaliseColors() {
        m_colorIndex = 0;
        m_colors = new Vector<Color>();

        int increment = 32;
        for (int r = increment*2; r < 255; r += increment) {
            for (int g = increment*2; g < 255; g += increment) {
                for (int b = increment*2; b < 255; b += increment) {
                    m_colors.add(new Color(r, g, b));
                }
            }
        }
    }


    public void setSelectedPath(TreePath selectedPath, DefaultMutableTreeNode centerNode) {
        if(selectedPath == null){
            clearAll();
            m_functionPanel.repaint();
            m_depthPanel.repaint();
            m_functionTimeLine.repaint();
            m_depthTimeLine.repaint();
        } else {
            getDebugInfo();
            m_routeSelection = selectedPath;
            notifyUpdateState();
            m_functionPanel.repaint();
            m_depthPanel.repaint();
            m_functionTimeLine.repaint();
            m_depthTimeLine.repaint();
        }
    }

    public void pushSelectedNode(DefaultMutableTreeNode selectedNode) {
        
    }
    
    
    
    private void drawTimeLine(Graphics2D g2){
        g2.setColor(Color.white);
        g2.fillRect(0, 0, m_functionTimeLine.getWidth(), m_functionTimeLine.getHeight());
        if(m_pathLength == 0) return;

        double totalUnits;
        if(showTime){
            totalUnits = m_pathLength*m_engine.getFrequency();
        } else {
            totalUnits = m_pathLength;
        }
        g2.setColor(Color.black);
        g2.drawLine(0, m_functionTimeLine.getHeight()/7, m_functionTimeLine.getWidth(), m_functionTimeLine.getHeight()/7);
        
        int stepSize = 0;
        
        double mulFactor = Math.pow(10.0, Math.ceil(Math.log10(totalUnits)));
        
        stepSize = (int) (mulFactor / (2-Math.floor((totalUnits*2.0)/mulFactor)))/20;
        if(stepSize == 0) stepSize =1;
        int i=0;
        
        while(i<totalUnits){
            int fontHeight = (int) g2.getFontMetrics().getStringBounds("" + (int)i, g2).getHeight();
            int along = (int)(m_functionTimeLine.getWidth()*i/totalUnits);
            g2.drawLine(along, 0, along, m_functionTimeLine.getHeight()/3);
            if(along + (int) g2.getFontMetrics().getStringBounds("" + (int)i, g2).getWidth() < m_functionTimeLine.getWidth())
            g2.drawString("" + (int)i, along , m_functionTimeLine.getHeight()/3 +fontHeight);
            
            i+=stepSize;
        }
        
        g2.drawLine(m_functionTimeLine.getWidth(), 0, m_functionTimeLine.getWidth(), m_functionTimeLine.getHeight()/3);
    }
    
}


class XtaCodeBlock {
    private String m_function;
    private int m_length;
    private int m_pausable;
    private int m_fNops;
    private int m_depth;
    private boolean m_selected;
    public XtaCodeBlock(String function, int length,
            int depth, int pausable, int fNops, boolean selected) {
        m_function = function;
        m_length = length;
        m_depth = depth;
        m_pausable = pausable;
        m_fNops = fNops;
        m_selected = selected;
    }
    public String getFunction() {
        return m_function;
    }
    public int getLength() {
        return m_length;
    }
    public int getDepth() {
        return m_depth;
    }
    public int getPausable() {
        return m_pausable;
    }
    public int getFNops() {
        return m_fNops;
    }
    public boolean getSelected() {
        return m_selected;
    }
    
    
    
}

class MouseOver implements MouseMotionListener {
    ArrayList<Box> m_boxes;
    JPanel m_functionPanel;
    
    MouseOver(ArrayList<Box> boxes, JPanel functionPanel) {
        m_boxes = boxes;
        m_functionPanel = functionPanel;
    }
    
    public void mouseMoved(MouseEvent e) {
        for(Box box : m_boxes) {
            if (box.withinBox(e.getX(), e.getY())) {
                String toolTipString = "";
                XtaCodeBlock block = (XtaCodeBlock)box.getParent();
                toolTipString += "<HTML>Block Function: " + block.getFunction() + "<br>";
                toolTipString += "<HTML>Depth: " + block.getDepth() + "<br>";
                toolTipString += "<HTML>FNOP Count: " + block.getFNops() + "<br>";
                toolTipString += "<HTML>Pausable Instructions: " + block.getPausable() + "<br>";
                toolTipString += "<HTML>Block Length: " + block.getLength() + "<br>";
                m_functionPanel.setToolTipText(toolTipString);
                return;
            }
        }
    }
    
    public void mouseDragged(MouseEvent arg0) {
    }
}
