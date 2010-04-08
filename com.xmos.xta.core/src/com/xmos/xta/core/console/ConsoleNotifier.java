package com.xmos.xta.core.console;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Notifier;

public class ConsoleNotifier extends Notifier {
	private Console m_console;

	public ConsoleNotifier(Console console, Config config) {
		super(config);
		m_console = console;
	}

	public void notify(CommandSource src, String message, String argument) {
		String level = getLevel(message);
		
		if (level.equals(NOTIFY_ERROR)) {
			m_console.display("xta: ");
			if (src.getLineNum() > 0) {
				m_console.display(src.getFileName() + ":" + src.getLineNum() + ": ");
			}
			m_console.display("error: " + argument + "\n");

		} else if (level.equals(NOTIFY_WARNING)) {
			m_console.display("xta: ");
			if (src.getLineNum() > 0) {
				m_console.display(src.getFileName() + ":" + src.getLineNum() + ": ");
			}
			m_console.display("warning: " + argument + "\n");

		} else if (level.equals(NOTIFY_INFO)) {
			m_console.display("xta: ");
			if (src.getLineNum() > 0) {
				m_console.display(src.getFileName() + ":" + src.getLineNum() + ": ");
			}
			m_console.display("info: " + argument + "\n");

		} 	
	}
}
