package com.xmos.xta.core.console.commands.assertion;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Assert extends CommandContainer {
	static private Command[] m_options = { 
		new Path(), 
		new Function(), 
	};

	public Assert() { super("assert"); }
	public Assert(Command command) { super("assert", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Assert timing"; }
	protected Command clone(Command command) { return new Assert(command); }	
}
