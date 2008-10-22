package com.xmos.xta.gui;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class ConsoleThread extends Thread {
    JDialog m_consoleFrame;
	Shell m_shell;
	Vector <String> m_commandHistory;
	GlobalConfig m_globalConfig;
	
	
	
	ConsoleThread(Engine engine, GlobalConfig globalConfig, JFrame mainFrame) {
    	JTextArea textArea = new JTextArea();    
    	m_globalConfig = globalConfig;
    	m_consoleFrame = new JDialog(mainFrame, "Console");
    	
    	m_consoleFrame.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
    	m_consoleFrame.addWindowListener(new WindowAdapter(){
            public void windowClosed(WindowEvent e) {
                m_shell.exit();
                
            }
        });
    	
    	m_consoleFrame.addComponentListener(new ComponentAdapter(){
            public void componentMoved(ComponentEvent e) {
                m_globalConfig.setM_consoleLocationOnScreen(e.getComponent().getLocationOnScreen());
            }
            public void componentResized(ComponentEvent e) {
                m_globalConfig.setM_consoleFrameDimensions(e.getComponent().getSize());
            }
        });
    	m_consoleFrame.add(textArea);
    	m_consoleFrame.add(new JScrollPane(textArea), BorderLayout.CENTER);
    	m_consoleFrame.setPreferredSize(m_globalConfig.getM_consoleFrameDimensions());
    	m_consoleFrame.setLocation(m_globalConfig.getM_consoleLocationOnScreen());
    	m_consoleFrame.pack();
    	m_consoleFrame.setVisible(true);       
        m_shell = new Shell(new ConsoleTextArea(textArea), engine);			
	}
	
    public void run() {
        m_shell.setHistoryStrings(m_globalConfig.getM_commandHistory());
        m_shell.execute();
        m_commandHistory = m_shell.getHistoryStrings();
        m_globalConfig.setM_commandHistory(m_commandHistory);
    	m_consoleFrame.dispose();
    }
    
    public Vector<String> getCommandHistory(){
        return m_commandHistory;
    }
}
