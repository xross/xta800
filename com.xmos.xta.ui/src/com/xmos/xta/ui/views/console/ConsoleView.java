package com.xmos.xta.ui.views.console;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.ui.GuiEngine;

public class ConsoleView extends ViewPart implements IPropertyChangeListener {
    public static final String ID = "com.xmos.xta.ui.views.console.Console";

    private GuiConsole m_guiConsole;
    
	public ConsoleView() {
		GuiEngine.addBroadcastListener(this);
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

	public void propertyChange(PropertyChangeEvent event) {
	}
	
    public synchronized void runCommand(String command) {
		m_guiConsole.sendCommand(command);
    }
}
