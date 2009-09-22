package com.xmos.xta.ui.views.console;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.GuiEngine;

public class GuiConsoleNotifier implements Notifier {
	private Console m_console;
	
	GuiConsoleNotifier(Console console) {
		m_console = console;
	}		
	
	public void notify(CommandSource src, String message, String argument) {
		if (message.equals(NOTIFY_ERROR)) {
			m_console.display("xta: ");		
			if (src.m_lineNum > 0) {
				m_console.display(src.m_fileName + ":" + src.m_lineNum + ": ");
			}
			m_console.display("error: " + argument + "\n");		
			
		} else if (message.equals(NOTIFY_WARNING)) {
			m_console.display("xta: ");		
			if (src.m_lineNum > 0) {
				m_console.display(src.m_fileName + ":" + src.m_lineNum + ": ");
			}
			m_console.display("warning: " + argument + "\n");		
			
		} else if (message.equals(NOTIFY_INFO)) {
			m_console.display("xta: ");		
			if (src.m_lineNum > 0) {
				m_console.display(src.m_fileName + ":" + src.m_lineNum + ": ");
			}
			m_console.display("info: " + argument + "\n");

		}
		GuiEngine.broadcast(message, argument);	
	}
}
