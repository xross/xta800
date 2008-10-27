package com.xmos.xta.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeobjectdisplayer.SourceDisplayer;
import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xta.gui.codeflow.BlockDecomposer;
import com.xmos.xta.gui.codestructure.CodeStructure;
import com.xmos.xta.gui.core.PathStatus;
import com.xmos.xta.gui.trees.TreeRouteDisplayer;

public class XtaGui extends Displayer {

    public final static boolean    DEBUG = false;
    
    private GlobalConfig            m_globalConfig;    
    private Config                  m_config;
    private Engine                  m_engine;
    private JTabbedPane             m_sourceTabs;
    private JPanel                  m_selectedPathSummary;
    private TreeRouteDisplayer      m_routeTree;
    private StatusBarDisplayer      m_statusBarDisplayer;
    private ToolBar                 m_toolBar;
    private CodeStructure           m_circleStructure;
    private OverallRouteDisplayer   m_overallRouteDisplayer;
    private JSplitPane              h_splitPane;
    private JPanel                  v_splitPane;
    private JSplitPane              main_splitPane;
    private PathStatus              m_pathStatus;
    private XtaXeObjectDisplayer    m_displayers;
    private Color                   m_lookAndFeelBackgroundColour;
    private JFrame                  m_mainFrame;
    private Menu                    m_menu;
    private BlockDecomposer         m_blockDecomposer;
    private Vector<ConsoleTextArea> m_consoles;
    private ConsoleArea             m_consoleArea;
    
    public static void main(String[] args) {
        XtaGui xtaGui = new XtaGui();
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error: could not set the system look and feel.");
        }
        xtaGui.guiStart();
    }

    private void guiStart(){
        m_globalConfig = new GlobalConfig();
        openUserSettings();
        m_config = new Config();
        m_engine = new Engine(m_config);
        m_engine.registerDisplayer(this);
        m_mainFrame = new JFrame();
        m_mainFrame.addWindowListener(new WindowAdapter(){
            public void windowClosed(WindowEvent e) {
                writeOutSettings(m_globalConfig);
                for (ConsoleTextArea consoleTextArea : m_consoles) {
                    consoleTextArea.sendCommand("exit");
                }
            }
        });

        m_mainFrame.addComponentListener(new ComponentAdapter(){
            public void componentMoved(ComponentEvent e) {
                m_globalConfig.setLocationOnScreen(e.getComponent().getLocationOnScreen());
            }
            public void componentResized(ComponentEvent e) {
                m_globalConfig.setMainFrameDimensions(e.getComponent().getSize());
            }
        });

        m_menu = new Menu(m_globalConfig, m_engine, m_mainFrame, this);
        m_mainFrame.setJMenuBar(m_menu);

        m_lookAndFeelBackgroundColour = m_mainFrame.getContentPane().getBackground();

        //load the global settings from the saved file

        //engine displayers
        m_statusBarDisplayer    = new StatusBarDisplayer(m_engine, m_mainFrame, m_globalConfig);
        m_pathStatus            = new PathStatus(m_globalConfig, m_engine, m_mainFrame, this);
        
        
        //path Updaters
        m_routeTree             = new TreeRouteDisplayer(m_pathStatus);
        m_circleStructure       = new CodeStructure(m_pathStatus, m_globalConfig);
        m_overallRouteDisplayer = new OverallRouteDisplayer(m_pathStatus);
        m_blockDecomposer       = new BlockDecomposer(m_pathStatus);

        m_pathStatus.registerPathUpdater(m_routeTree);
        m_pathStatus.registerPathUpdater(m_circleStructure);
        m_pathStatus.registerPathUpdater(m_overallRouteDisplayer);
        m_pathStatus.registerPathUpdater(m_blockDecomposer);

        m_mainFrame.getContentPane().setLayout(new GridBagLayout());
        ImageIcon xmosLogo = null;
        try {
            xmosLogo = new ImageIcon(XtaGui.class.getResource("XmosTM.jpg"));
        } catch (Exception e) {
            System.err.println("Xmos logo missing.");
        }
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(xmosLogo);
        m_mainFrame.getContentPane().add(iconLabel);
        m_mainFrame.getContentPane().setBackground(Color.white);
        m_mainFrame.setVisible(true);
        m_mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if (m_globalConfig.getMainFrameDimensions() != null) {
            m_mainFrame.setLocation(m_globalConfig.getLocationOnScreen());
            m_mainFrame.setPreferredSize(m_globalConfig.getMainFrameDimensions());
        } else {
            m_mainFrame.setPreferredSize(new Dimension(1024, 800));
        }

        try {
            m_mainFrame.setIconImage(new ImageIcon(XtaGui.class.getResource("xta.gif")).getImage());
        } catch (Exception e1) {
            System.out.println("Xmos icon missing.");
        }
        m_mainFrame.pack();
        m_engine.registerDisplayer(m_statusBarDisplayer);
        m_engine.registerDisplayer(m_pathStatus);
        
        m_consoles = new Vector<ConsoleTextArea>();
        
        if(DEBUG){
            m_menu.open(m_globalConfig.getLastOpenedFile());
        }
    }

    public void buildGui(){
        m_mainFrame.getContentPane().setLayout(new BorderLayout());
        m_displayers = new XtaXeObjectDisplayer(m_engine.getXeLoader(), m_menu, m_menu, m_engine);
        m_globalConfig.setM_sourceFileNames(m_displayers.getSourceFiles());
        m_pathStatus.setXeObjectDisplayer(m_displayers);
        m_toolBar = new ToolBar(m_globalConfig, m_displayers, m_engine);
        m_mainFrame.getContentPane().removeAll();
        m_mainFrame.getContentPane().setBackground(m_lookAndFeelBackgroundColour);
        m_mainFrame.setVisible(true);
        if (m_globalConfig.getMainFrameDimensions() != null) {
            m_mainFrame.setLocation(m_globalConfig.getLocationOnScreen());
            m_mainFrame.setPreferredSize(m_globalConfig.getMainFrameDimensions());
        }

        m_mainFrame.getContentPane().add(m_toolBar, BorderLayout.NORTH);
        m_selectedPathSummary = m_overallRouteDisplayer.getPanel();
        main_splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, makeCodeEnvironment(), m_selectedPathSummary);
        main_splitPane.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent p) {
                if (p.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
                    m_globalConfig.setMainSplitPaneDivision(main_splitPane.getDividerLocation());
                }
            }
        });
        m_mainFrame.getContentPane().add(main_splitPane, BorderLayout.CENTER);
        m_mainFrame.getContentPane().add(m_statusBarDisplayer.getPanel(), BorderLayout.SOUTH);
        setupUserLookAndFeel();
        m_mainFrame.pack();
        
    }

    private void openUserSettings(){
        //look for a users setting file
        String fileName = "XtaSettings.xml";
        File settingFile = new File(fileName);
        boolean exists = settingFile.exists();
        if (exists){
            //load settings
            try {
                m_globalConfig = readSettings(fileName);
            } catch (Exception e) {
                System.err.println("problems reading settings file - default used");
                e.printStackTrace();
            }
        } else {
            m_globalConfig = new GlobalConfig();
        }
        m_globalConfig.setDefaultSettings();
    }


    public void setupUserLookAndFeel(){
        main_splitPane.setDividerLocation(m_globalConfig.getMainSplitPaneDivision());
        h_splitPane.setDividerLocation(m_globalConfig.getHSplitPaneDivision());
    }

    public static GlobalConfig readSettings(String filename) throws Exception {
        XMLDecoder decoder =
            new XMLDecoder(new BufferedInputStream(
                    new FileInputStream(filename)));
        GlobalConfig o = (GlobalConfig)decoder.readObject();
        decoder.close();
        return o;
    }

    private JPanel makeCodeEnvironment(){
        JPanel codePanel = new JPanel();
        codePanel.setLayout(new BorderLayout());

        //build the code area
        m_sourceTabs = new JTabbedPane();
        for(SourceDisplayer d : m_displayers.getSourceDisplayers())
            m_sourceTabs.add(d, d.getTitle());
        m_sourceTabs.add(m_displayers.getDisassemblyDisplayer(), m_displayers.getDisassemblyDisplayer().getTitle());
        final JPanel circleStructure = m_circleStructure.getJPanel();
        final JPanel codeFlow = m_blockDecomposer.getJPanel();
        
        // If this has already been called before, then ensure that the previous
        // console area is unregistered from the xtaGui.
        if (m_consoleArea != null) {
        	unregisterConsole(m_consoleArea.getConsoleTextArea());
        }
        m_consoleArea = new ConsoleArea(m_engine, m_globalConfig, false);
        
        m_sourceTabs.add(circleStructure, "Code Structure");
        m_sourceTabs.add(codeFlow, "Code Flow");
        m_sourceTabs.add(m_consoleArea.getScrollPane(), "Console");           

        new Thread() {
    	    public void run() {        
    	        registerConsole(m_consoleArea.getConsoleTextArea());
    	    	m_consoleArea.execute();        
		    }
    	}.start();    	    	
        v_splitPane= m_routeTree.getPanel();
        v_splitPane.revalidate();
        h_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, v_splitPane, m_sourceTabs);
        h_splitPane.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent p) {
                if (p.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY))
                {
                    m_globalConfig.setHSplitPaneDivision(h_splitPane.getDividerLocation());
                }
            }
        });
        codePanel.add(h_splitPane, BorderLayout.CENTER);
        codePanel.setPreferredSize(new Dimension(800, 600));
        return codePanel;
    }


    public void displayRoute(Route route, DebugInfo debugInfo,
            MemoryImage memoryImage, SymbolTable table) {
    }

    public void notifyAnalysePaths() {      
    }

    public void notifyLoadExecutable() {
        buildGui(); 
    }

    public void notifyUpdateState() { 
        m_routeTree.getPanel().repaint();
    }    

    public void registerConsole(ConsoleTextArea console) {
    	m_consoles.add(console);
    }

    public void unregisterConsole(ConsoleTextArea console) {
    	m_consoles.remove(console);
    }

    public void sendToConsole(String command) {
    	assert(m_consoles.size() > 0);
    	m_consoles.get(0).sendCommand(command);
    	m_sourceTabs.setSelectedComponent(m_consoleArea.getScrollPane());
    }        
    
    private void writeOutSettings(GlobalConfig globalConfig){
        XMLEncoder xml = null;
        try {
            xml = new XMLEncoder( new BufferedOutputStream(
                    new FileOutputStream("XtaSettings.xml")));
        } 
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        xml.writeObject(globalConfig);
        xml.flush();
        xml.close();
    }
}
