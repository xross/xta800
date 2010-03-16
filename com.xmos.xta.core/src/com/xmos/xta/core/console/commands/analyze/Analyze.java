package com.xmos.xta.core.console.commands.analyze;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Analyze extends CommandContainer {
	private static final String[] COMMAND_NAMES = { "analyze", "analyse" };

	static private Command[] m_options = {
		new Function(),
		new Path(),
	};

	public Analyze() { super(COMMAND_NAMES); }
	public Analyze(Command command) { super(COMMAND_NAMES, command); }

	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Perform timing analysis"; }
	protected Command clone(Command command) { return new Analyze(command); }
}
