package com.xmos.xta.console;

import com.xmos.xta.engine.ProgressMonitor;

public class ConsoleProgessMonitor implements ProgressMonitor {
	private Console m_console;
	
	ConsoleProgessMonitor(Console console) {
		m_console = console;
	}
	
	public void beginTask(String name, int totalWork) {
	}

	public boolean isCanceled() {
		return false;
	}

	public void subTask(String name) {
		m_console.display(name + "\n");
	}

	public void worked(int work) {
	}
}
