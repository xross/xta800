package com.xmos.xta.console.commands;

class Analyse extends CommandContainer {
	static private Command[] m_options = { 
		new Path(), 
		new Function(), 
	};
	
	Analyse() { super("analyse"); }
	Analyse(Command command) { super("analyse", command); }
	
	protected String getDescription() { return "Perform timing analysis"; }
	protected Command[] getSubCommands() { return m_options; }
	protected Command clone(Command command) { return new Analyse(command); }	
}
