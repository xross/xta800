package com.xmos.xta.console.commands.unset;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;
import com.xmos.xta.console.commands.set.Required;

public class Unset extends CommandContainer {
	static private Command[] m_options = { 
		new Required(Required.Operation.UNSET), 
	};
	
	public Unset() { super("unset"); }
	public Unset(Command command) { super("unset", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Unset item from given list"; }
	protected Command clone(Command command) { return new Unset(command); }	
}
