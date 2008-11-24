package com.xmos.xta.swing;

import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class ConsoleArea {
    private Shell m_shell;
    private Vector <String> m_commandHistory;
    private GlobalConfig m_globalConfig;
    private JTextArea m_textArea;
    private JScrollPane m_scrollPane;
    private ConsoleTextArea m_consoleTextArea;
	
    ConsoleArea(Engine engine, GlobalConfig globalConfig, boolean exitAllowed) {
        m_textArea = new JTextArea();
        m_scrollPane = new JScrollPane(m_textArea);
    	m_consoleTextArea = new ConsoleTextArea(m_textArea);    	
    	m_globalConfig = globalConfig;
        m_shell = new Shell(m_consoleTextArea, engine, exitAllowed);		        
	}
	
    public void execute() {
        m_shell.setHistoryStrings(m_globalConfig.getM_commandHistory());
        m_shell.execute();
        m_commandHistory = m_shell.getHistoryStrings();
        m_globalConfig.setM_commandHistory(m_commandHistory);
    }
    
    public Vector<String> getCommandHistory(){
        return m_commandHistory;
    }
    
    public ConsoleTextArea getConsoleTextArea() {
    	return m_consoleTextArea;
    }

    public JScrollPane getScrollPane() {
    	return m_scrollPane;
    }
}
