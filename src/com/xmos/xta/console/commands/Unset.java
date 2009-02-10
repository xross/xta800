package com.xmos.xta.console.commands;

class Unset extends CommandContainer {
	static private Command[] m_options = { 
		new Time(Time.Operation.UNSET), 
	};
	
	Unset() { super("unset"); }
	Unset(Command command) { super("unset", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Unset item from given list"; }
	protected Command clone(Command command) { return new Unset(command); }	
}
