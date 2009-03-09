package com.xmos.xta.console.commands.Unset;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;
import com.xmos.xta.console.commands.MaxTime;
import com.xmos.xta.console.commands.Time;

public class Unset extends CommandContainer {
	static private Command[] m_options = { 
		new Time(Time.Operation.UNSET), 
		new MaxTime(MaxTime.Operation.UNSET), 
	};
	
	public Unset() { super("unset"); }
	public Unset(Command command) { super("unset", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Unset item from given list"; }
	protected Command clone(Command command) { return new Unset(command); }	
}
