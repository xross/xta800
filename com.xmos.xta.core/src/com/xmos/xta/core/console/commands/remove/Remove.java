package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Remove extends CommandContainer {
	private static final String[] COMMAND_NAMES = { "remove" };

	static private Command[] m_options = {
		new Branch(),
		new Core(),
		new Exclusion(),
		new FunctionTime(),
		new InstructionTime(),
		new Loop(),
		new LoopPath(),
		new LoopScope(),
		new PathTime(),
		new Route(),
	};

	public Remove() { super(COMMAND_NAMES); }
	public Remove(Command command) { super(COMMAND_NAMES, command); }

	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Remove item from given list"; }
	protected Command clone(Command command) { return new Remove(command); }
}
