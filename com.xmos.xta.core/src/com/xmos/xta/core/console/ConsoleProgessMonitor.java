package com.xmos.xta.core.console;

import com.xmos.xta.core.engine.ProgressMonitor;

public class ConsoleProgessMonitor implements ProgressMonitor {
	private Console m_console;
	
	ConsoleProgessMonitor(Console console) {
		m_console = console;
	}
	
	public void beginTask(String name, int totalWork) {
	}

	public void setTaskName(String name) {
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
