package com.xmos.xta.console.commands.config;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;

public class Config extends CommandContainer {
	static private Command[] m_options = { 
		new Proc(), 
		new Freq(), 
		new Threads(), 
		new SrcPaths(), 
	};
	
	public Config() { super("config"); }
	public Config(Command command) { super("config", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Configures the given option"; }
	protected Command clone(Command command) { return new Config(command); }
}
