package com.xmos.xta.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.xmos.xta.engine.Engine;

@SuppressWarnings("serial")
public class ToolBar extends JPanel {

    private Engine m_engine;
    private XeObjectDisplayer m_displayers;
    private JToolBar toolBar;
    private Vector<String> m_functionNames;
    private boolean allFolded = false;
    private JButton pathButton;
    private JComboBox functionComboBox;
    private GlobalConfig m_globalConfig;
    private JButton foldAll;
    
    private JButton FNOPStatusButton;
    
    private JFormattedTextField m_threadCountBox;
    private JFormattedTextField m_frequencyBox;
    
    private String m_path   = null;
    
    private int m_frequency = 400;
    private int m_threadCount = 8;
    
    public ToolBar(GlobalConfig globalConfig, XeObjectDisplayer displayers, Engine engine) {
        super();
        m_displayers = displayers;
        m_engine = engine;
        m_globalConfig = globalConfig;
        int nodeId = engine.getNodeId();
        int coreId = engine.getCoreId();
        m_functionNames = engine.getXeLoader().getFunctionNames(nodeId, coreId);
        init();
    }
    
    private void init(){
        pathButton = new JButton("Analyse Path");
        foldAll = new JButton("Unfold All");
        foldAll.setToolTipText("<HTML>Folds/unfolds all the assembly<br> from the source code<HTML>");
        FNOPStatusButton = new JButton("Hide FNOPs");
        
        
        m_threadCountBox = new JFormattedTextField();
        m_threadCountBox.setColumns(3);
        m_threadCountBox.addPropertyChangeListener(new PropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent evt) {
                    try {
                        m_threadCount = Integer.parseInt(m_threadCountBox.getText());
                    } catch (Exception e) {
                        m_threadCount = 8;
                        m_threadCountBox.setText("8");
                    }
                    m_engine.setNumThreads(m_threadCount);
            }
        });
        
        m_frequencyBox = new JFormattedTextField();
        m_frequencyBox.setColumns(3);
        m_frequencyBox.addPropertyChangeListener(new PropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent evt) {
                    try {
                        m_frequency = Integer.parseInt(m_frequencyBox.getText());
                    } catch (Exception e) {
                        m_frequency = 400;
                        m_frequencyBox.setText("400");
                    }
                    m_engine.setFrequency(m_frequency);
            }
        });
        
        
        FNOPStatusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_globalConfig.setFNOPS(! m_globalConfig.getFNOPS());
                String text = null;
                
                if(m_globalConfig.getFNOPS() == false){
                    text = "Hide FNOPs";
                    GuiConfig.fnopsShown = true;
                } else {
                    text = "Show FNOPs";
                    GuiConfig.fnopsShown = false;
                }
                
                FNOPStatusButton.setText(text);
                m_displayers.redraw();
        }});
        
        foldAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                foldAllPressed();
        }});
        
        pathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pathButtonPressed();
        }});
        
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(new JLabel("Function: "));
        functionComboBox = new JComboBox();
        for(String functionName : m_functionNames){
            functionComboBox.addItem(functionName);
        }
        
        functionComboBox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                RunEngineMethodFunc m = new RunEngineMethodFunc(functionComboBox.getSelectedItem().toString());
                m.start();
            }            
        });
        
        toolBar.add(functionComboBox);
        toolBar.addSeparator();
        toolBar.add(pathButton);
        toolBar.addSeparator();
        toolBar.add(foldAll);
        
        toolBar.add(FNOPStatusButton);
        toolBar.addSeparator();
        new JLabel(m_path);
        toolBar.add(new JLabel("Frequency: "));
        toolBar.add(m_frequencyBox);
        
        toolBar.add(new JLabel(" MHz  Thread Count: "));
        toolBar.add(m_threadCountBox);
        this.setLayout(new BorderLayout());
        this.add(toolBar, BorderLayout.WEST);
        
    }

    private void foldAllPressed(){
        if(allFolded){
            foldAll.setText("Unfold All");
        } else {
            foldAll.setText("Fold All");
        }
        m_displayers.setFoldAll(allFolded);
        allFolded = ! allFolded;
    }

    private void pathButtonPressed(){
        
        if(GuiConfig.startLine!=null && GuiConfig.endLine !=null) {
            String start = GuiConfig.startLine.replaceFirst("^.*:0x", "0x");
            String end = GuiConfig.endLine.replaceFirst("^.*:0x", "0x");
            RunEngineMethodRef m = new RunEngineMethodRef(start, end);
            m.start();
        }
    }
    
    class RunEngineMethodFunc extends Thread
    {
    	String m_functionName;
    	
    	RunEngineMethodFunc(String functionName) {
    		m_functionName = functionName;
    	}
    	
        public void run() {
            if (!m_engine.analyseRoute(m_functionName))
                System.err.println(m_engine.getErrorString());
        }
    }
        
    class RunEngineMethodRef extends Thread
    {
    	String m_startRef;
    	String m_endRef;
    	
    	RunEngineMethodRef(String startRef, String endRef) {
    		m_startRef = startRef;
    		m_endRef = endRef;
    	}
    	
        public void run() {        	
            if (!m_engine.analyseRoute(m_startRef, m_endRef))
                System.err.println(m_engine.getErrorString());
        }
    }    



}
