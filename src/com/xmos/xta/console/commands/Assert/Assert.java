package com.xmos.xta.console.commands.Assert;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.CommandContainer;

public class Assert extends CommandContainer {
	static private Command[] m_options = { 
		new MaxTime(), 
		new MaxCycles(), 
	};
	
	public Assert() { super("assert"); }
	public Assert(Command command) { super("assert", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Set limits on the analysis phase"; }
	protected Command clone(Command command) { return new Assert(command); }	
}
