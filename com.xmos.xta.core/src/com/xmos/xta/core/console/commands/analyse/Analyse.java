package com.xmos.xta.core.console.commands.analyse;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Analyse extends CommandContainer {
	static private Command[] m_options = {
		new Function(),
		new Path(),
	};

	public Analyse() { super("analyse"); }
	public Analyse(Command command) { super("analyse", command); }

	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Perform timing analysis"; }
	protected Command clone(Command command) { return new Analyse(command); }
}
