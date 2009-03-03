package com.xmos.xta.console;

import com.xmos.xta.engine.Notifier;

public class ConsoleNotifier implements Notifier {
	private Console m_console;
	
	ConsoleNotifier(Console console) {
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
	}
}
