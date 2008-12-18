package com.xmos.xta.console.commands;

class Config extends CommandContainer {
	static private Command[] m_options = { 
		new Proc(), 
		new Freq(), 
		new Threads(), 
		new Iterations(), 
		new Mode(), 
	};
	
	Config() { super("config"); }
	Config(Command command) { super("config", command); }
	
	protected String getDescription() { return "Configures the given option"; }
	protected Command[] getSubCommands() { return m_options; }
	protected Command clone(Command command) { return new Config(command); }
}
