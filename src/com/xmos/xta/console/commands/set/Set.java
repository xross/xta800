package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;

public class Set extends CommandContainer {
	static private Command[] m_options = { 
		new Loop(Loop.Operation.SET),
		new LoopScope(), 
		new Time(Time.Operation.SET), 
		new Required(Required.Operation.SET), 
		new Exclusion(Exclusion.Operation.SET),
	};
	
	public Set() { super("set"); }
	public Set(Command command) { super("set", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Set path information"; }
	protected Command clone(Command command) { return new Set(command); }	
}
