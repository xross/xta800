package com.xmos.xta.console.commands;

class Assert extends CommandContainer {
	static private Command[] m_options = { 
		new MaxTime(), 
		new MaxCycles(), 
	};
	
	Assert() { super("assert"); }
	Assert(Command command) { super("assert", command); }
	
	protected String getDescription() { return "Set limts on the analysis phase"; }
	protected Command[] getSubCommands() { return m_options; }
	protected Command clone(Command command) { return new Analyse(command); }	
}
