package com.xmos.xta.gui.views.console;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.GuiEngine;

public class GuiConsoleNotifier implements Notifier {
	private Console m_console;
	
	GuiConsoleNotifier(Console console) {
		m_console = console;
	}		
	
	public void notify(String message, String argument) {
		if (message.equals(NOTIFY_ERROR)) {
			m_console.display("Error: " + argument + "\n");		
			
		} else if (message.equals(NOTIFY_WARNING)) {
			m_console.display("Warning: " + argument + "\n");	
			
		} else if (message.equals(NOTIFY_INFO)) {
			m_console.display("Info: " + argument + "\n");			
		}		
		GuiEngine.broadcast(message, argument);	
	}
}
