package com.xmos.xta.console;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Route;

public class ConsoleDisplayer implements Displayer {
	private Console m_console;
	
	ConsoleDisplayer(Console console) {
		m_console = console;
	}
	
	public void displayRoute(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {				
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
