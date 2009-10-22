package com.xmos.xta.core.console.commands.assertion;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Assert extends CommandContainer {
	static private Command[] m_options = { 
		new Path(), 
		new Function(), 
	};

	public Assert() { super("analyse"); }
	public Assert(Command command) { super("analyse", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Assert timing requirement"; }
	protected Command clone(Command command) { return new Assert(command); }	
}
