package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Debug extends CommandContainer {
	private static final String[] COMMAND_NAMES = { "debug" };

	static private Command[] m_options = {
		new DumpControlFlow(),
		new VerifyReference(),
		new FromPoints(),
		new ToPoints(),

	};

	public Debug() { super(COMMAND_NAMES); }
	public Debug(Command command) { super(COMMAND_NAMES, command); }

	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Internal debug commands"; }
	protected Command clone(Command command) { return new Debug(command); }
}
