package com.xmos.xta.gui.views.console;

import com.xmos.xta.console.Console;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.GuiEngine;

public class GuiConsoleNotifier implements Notifier {
	private Console m_console;
	
	GuiConsoleNotifier(Console console) {
		m_console = console;
	}		
	
	public void notify(CommandSource src, String message, String argument) {
		if (message.equals(NOTIFY_ERROR)) {
			if (src.m_lineNum > 0) {
				m_console.display(src.m_fileName + ":" + src.m_lineNum + ": ");
			}
			m_console.display("error: " + argument + "\n");		
			
		} else if (message.equals(NOTIFY_WARNING)) {
			if (src.m_lineNum > 0) {
				m_console.display(src.m_fileName + ":" + src.m_lineNum + ": ");
			}
			m_console.display("warning: " + argument + "\n");		
			
		} else if (message.equals(NOTIFY_INFO)) {
			if (src.m_lineNum > 0) {
				m_console.display(src.m_fileName + ":" + src.m_lineNum + ": ");
			}
			m_console.display("info: " + argument + "\n");

		}
		GuiEngine.broadcast(message, argument);	
	}
}
