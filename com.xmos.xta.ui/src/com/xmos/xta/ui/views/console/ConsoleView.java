package com.xmos.xta.ui.views.console;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.CommandSource;

public class ConsoleView extends ViewPart {
    public static final String ID = "com.xmos.xta.ui.views.console.Console";

    private GuiConsole m_guiConsole;
    
	public ConsoleView() {
	}

	public void dispose() {
		super.dispose();
	}
	
	public void createPartControl(Composite parent) {
		m_guiConsole = new GuiConsole(parent);

		new Thread() {
	    	public void run() {
	    		m_guiConsole.execute();
    		}}.start();
    }

	public void setFocus() {
	}
	
    public synchronized void runCommand(String command, CommandSource src) {
		m_guiConsole.sendCommand(command, src);
    }

	public Console getConsole() {
		return m_guiConsole;
	}
}
