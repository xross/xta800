package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Set extends CommandContainer {
	private static final String[] COMMAND_NAMES = { "set" };

	static private Command[] m_options = {
		new From(),
		new FunctionTime(),
		new InstructionTime(),
		new Loop(),
		new LoopPath(),
		new LoopScope(),
		new PathTime(),
		new Required(),
		new To(),
	};

	public Set() { super(COMMAND_NAMES); }
	public Set(Command command) { super(COMMAND_NAMES, command); }

	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Set path information"; }
	protected Command clone(Command command) { return new Set(command); }
}
