package com.xmos.xta.core.console.commands.scripter;


import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Scripter extends CommandContainer {
	static private Command[] m_options = { 
		new Disable(),
		new Dump(),
		new Enable(),
		new ListRefs(),
		new Rename(),
	};
	
	public Scripter() { super("scripter"); }
	public Scripter(Command command) { super("scripter", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Work with the script generator"; }
	protected Command clone(Command command) { return new Scripter(command); }	
}
