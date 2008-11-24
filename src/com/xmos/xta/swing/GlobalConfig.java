package com.xmos.xta.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

public class GlobalConfig {

    private Boolean m_antiAlising;
    private LinkedList<String> m_fileHistory;
    private Point m_locationOnScreen;
    private Dimension m_mainFrameDimensions;
    private Integer m_vSplitPaneDivision;
    private Integer m_hSplitPaneDivision;
    private Integer m_mainSplitPaneDivision;
    private Vector<String> m_commandHistory;
    private Dimension m_consoleFrameDimensions;
    private Point m_consoleLocationOnScreen;
    private Integer m_codeStructureObjects;
    private Color m_highlightedRouteColour;
    private Color m_highlightedLoopColour;
    
    private ArrayList<String> m_sourceFileNames;
    
    private static final int fileListLength = 6;
    
    public GlobalConfig() {
        m_fileHistory = new LinkedList<String>();
    }

    public Boolean getAntiAlising() {
        return m_antiAlising;
    }

    public void setAntiAlising(Boolean antiAlising) {
        m_antiAlising = antiAlising;
    }

    public String getLastOpenedFile() {
        if (m_fileHistory.size()>0)
        return m_fileHistory.getLast();
        else return null;
    }

    public void setLastOpenedFile(String openedFile) {
        
        if (m_fileHistory.contains(openedFile)) {
            m_fileHistory.remove(m_fileHistory.indexOf(openedFile));
        }
        
        if (m_fileHistory.size()<fileListLength)
            m_fileHistory.add(openedFile);
        else {
            m_fileHistory.removeFirst();
            m_fileHistory.add(openedFile);
        }
    }
    
    public Integer getVSplitPaneDivision() {
        return m_vSplitPaneDivision;
    }

    public void setVSplitPaneDivision(Integer splitPaneDivision) {
        m_vSplitPaneDivision = splitPaneDivision;
    }

    public Integer getHSplitPaneDivision() {
        return m_hSplitPaneDivision;
    }

    public void setHSplitPaneDivision(Integer splitPaneDivision) {
        m_hSplitPaneDivision = splitPaneDivision;
    }

    public Integer getMainSplitPaneDivision() {
        return m_mainSplitPaneDivision;
    }

    public void setMainSplitPaneDivision(Integer mainSplitPaneDivision) {
        m_mainSplitPaneDivision = mainSplitPaneDivision;
    }

    public void setMainFrameDimensions(Dimension mainFrameDimensions) {
        m_mainFrameDimensions = mainFrameDimensions;
    }

    public Dimension getMainFrameDimensions() {
        return m_mainFrameDimensions;
    }

    public void setLocationOnScreen(Point locationOnScreen) {
        m_locationOnScreen = locationOnScreen;
    }

    public Point getLocationOnScreen() {
        return m_locationOnScreen;
    }

    public void setDefaultSettings() {
        if (m_antiAlising == null) {
            m_antiAlising = new Boolean(true);
        }
        if (m_fileHistory == null) {
            m_fileHistory = new LinkedList<String>();
        }
        if (m_locationOnScreen == null) {
            m_locationOnScreen = new Point(0,0);
        }
        if (m_mainFrameDimensions == null) {
            m_mainFrameDimensions = new Dimension(1024, 800);
        }
        if (m_vSplitPaneDivision == null) {
            m_vSplitPaneDivision = new Integer(400);
        }
        if (m_hSplitPaneDivision == null) {
            m_hSplitPaneDivision = new Integer(400);  
        }
        if (m_mainSplitPaneDivision == null) {
            m_mainSplitPaneDivision = new Integer(800);
        }
        if(m_commandHistory == null ){
            m_commandHistory = new Vector<String>();
        }
        if (m_consoleLocationOnScreen == null) {
            m_consoleLocationOnScreen = new Point(0,0);
        }
        if (m_consoleFrameDimensions == null) {
            m_consoleFrameDimensions = new Dimension(500, 800);
        }
        if(m_highlightedRouteColour == null){
            m_highlightedRouteColour = Color.pink;
        }
        if(m_highlightedLoopColour == null){
            m_highlightedLoopColour = Color.cyan;
        }
        if(m_sourceFileNames == null){
            m_sourceFileNames = new ArrayList<String>();
        }
        if(m_codeStructureObjects == null){
            m_codeStructureObjects = new Integer(2000);
        }
    }

    public void setFileHistory(LinkedList<String> fileHistory) {
        m_fileHistory = fileHistory;
    }

    public LinkedList<String> getFileHistory() {
        return m_fileHistory;
    }

    public void setM_commandHistory(Vector<String> m_commandHistory) {
        this.m_commandHistory = m_commandHistory;
    }

    public Vector<String> getM_commandHistory() {
        return m_commandHistory;
    }

    public void setM_consoleFrameDimensions(Dimension m_consoleFrameDimensions) {
        this.m_consoleFrameDimensions = m_consoleFrameDimensions;
    }

    public Dimension getM_consoleFrameDimensions() {
        return m_consoleFrameDimensions;
    }

    public void setM_consoleLocationOnScreen(Point m_consoleLocationOnScreen) {
        this.m_consoleLocationOnScreen = m_consoleLocationOnScreen;
    }

    public Point getM_consoleLocationOnScreen() {
        return m_consoleLocationOnScreen;
    }

    public void setM_highlightedRouteColour(Color m_highlightedRouteColour) {
        this.m_highlightedRouteColour = m_highlightedRouteColour;
    }

    public Color getM_highlightedRouteColour() {
        return m_highlightedRouteColour;
    }

    public void setM_highlightedLoopColour(Color m_highlightedLoopColour) {
        this.m_highlightedLoopColour = m_highlightedLoopColour;
    }

    public Color getM_highlightedLoopColour() {
        return m_highlightedLoopColour;
    }

    public void setM_sourceFileNames(ArrayList<String> m_sourceFileNames) {
        this.m_sourceFileNames = m_sourceFileNames;
    }

    public ArrayList<String> getM_sourceFileNames() {
        return m_sourceFileNames;
    }

    public void setM_codeStructureObjects(Integer m_codeStructureObjects) {
        this.m_codeStructureObjects = m_codeStructureObjects;
    }

    public Integer getM_codeStructureObjects() {
        return m_codeStructureObjects;
    }
    
   
    
}
