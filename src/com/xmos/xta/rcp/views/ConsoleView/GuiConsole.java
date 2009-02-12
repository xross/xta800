package com.xmos.xta.rcp.views.ConsoleView;

import java.util.Vector;

import org.eclipse.swt.widgets.Composite;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.rcp.XtaEngine;

public class GuiConsole implements Console {
    private Shell m_shell;
	private TextReader m_textReader;
	private ConsoleView m_consoleView; 
	
	public GuiConsole(ConsoleView console, Composite parent) {
		m_consoleView = console;
		m_shell = new Shell(this, XtaEngine.getEngine(), false);		        
		m_textReader = new TextReader(parent, m_shell);
	}
	
    public void execute() {
        m_shell.execute();
    }
	
	public void display(String str) {		
		m_textReader.display(str);
	}
	
	public void displayPrompt(String str) {
		m_textReader.displayPrompt(str);
	}
	
	public String getNextLine(Vector<Command> commandHistory) {
		if (m_consoleView.hasPendingCommands()) {
			String command = m_consoleView.getCommand();
			display(command);
	        return command;
		} else {
			String command = m_textReader.readLine(commandHistory);
	        return command;
		}
	}
	
	public boolean wasInternal() {
		return m_textReader.wasInternal();
	}
	
	public void clearScreen() {
		m_textReader.clearScreen();		
	}
	
	public void sendCommand(String command) {
		m_textReader.sendCommand(command);
	}	
}

