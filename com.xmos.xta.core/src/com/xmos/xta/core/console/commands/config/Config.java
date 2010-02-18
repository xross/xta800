package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Config extends CommandContainer {
	static private Command[] m_options = {
		new Case(),
		new Core(),
		new Freq(),
		new Scale(),
		new SrcPaths(),
		new Threads(),
		new TimeOut(),
		new TimingErrors(),
		new Verbosity(),
		new WarnAsErrors(),
	};

	public Config() { super("config"); }
	public Config(Command command) { super("config", command); }

	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Configures the given option"; }
	protected Command clone(Command command) { return new Config(command); }
}
