package com.xmos.xta.core.console.commands.scripter;


import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Scripter extends CommandContainer {
	private static final String[] COMMAND_NAMES = { "scripter" };

	static private Command[] m_options = {
		new Disable(),
		new Dump(),
		new Embed(),
		new Enable(),
		new ListRefs(),
		new Rename(),
	};

	public Scripter() { super(COMMAND_NAMES); }
	public Scripter(Command command) { super(COMMAND_NAMES, command); }

	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Work with the script generator"; }
	protected Command clone(Command command) { return new Scripter(command); }
}
