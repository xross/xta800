package com.xmos.xta.console.commands;

class Assert extends CommandContainer {
	static private Command[] m_options = { 
		new MaxTime(), 
		new MaxCycles(), 
	};
	
	Assert() { super("assert"); }
	Assert(Command command) { super("assert", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Set limits on the analysis phase"; }
	protected Command clone(Command command) { return new Assert(command); }	
}
