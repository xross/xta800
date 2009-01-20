package com.xmos.xta.console.commands;

class Set extends CommandContainer {
	static private Command[] m_options = { 
		new Loop(), 
		new Time(), 
	};
	
	Set() { super("set"); }
	Set(Command command) { super("set", command); }
	
	protected String getDescription() { return "Set path information"; }
	protected Command[] getSubCommands() { return m_options; }
	protected Command clone(Command command) { return new Set(command); }	
}
