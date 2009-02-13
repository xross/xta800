package com.xmos.xta.console.commands.Set;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.CommandContainer;
import com.xmos.xta.console.commands.Time;

public class Set extends CommandContainer {
	static private Command[] m_options = { 
		new Loop(), 
		new Time(Time.Operation.SET), 
	};
	
	public Set() { super("set"); }
	public Set(Command command) { super("set", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Set path information"; }
	protected Command clone(Command command) { return new Set(command); }	
}
