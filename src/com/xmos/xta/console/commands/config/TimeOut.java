package com.xmos.xta.console.commands.config;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class TimeOut extends Command {
	private int m_time;

	public TimeOut() {
		m_time = 0;
	}

	public TimeOut(int time) {
		m_time = time;
	}

	public int numArgs() {
		return 1;
	}

	public String name() {
		return "timeout";
	}

	public String commandLine() {
		return String.format("timeout %d", m_time);
	}

	public String description(Engine engine, boolean detailed) {
		return "timeout <seconds>: Sets the tools timeout on load";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

		int time;
		try {
			time = Integer.parseInt(args[index + 1]);
		} catch (NumberFormatException e) {
			return null;
		}
		return new TimeOut(time);
	}

	public void execute(Shell shell, Engine engine, Console console) {
		engine.setAnalysisTimeOut(m_time);
	}
}
