package com.xmos.xta.console.commands.Set;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;
import com.xmos.xta.console.commands.MaxTime;
import com.xmos.xta.console.commands.Time;
import com.xmos.xta.console.commands.Constraint;

public class Set extends CommandContainer {
	static private Command[] m_options = { 
		new Loop(), 
		new Time(Time.Operation.SET), 
		new Constraint(Constraint.Operation.SET),
		new MaxTime(MaxTime.Operation.SET), 
	};
	
	public Set() { super("set"); }
	public Set(Command command) { super("set", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Set path information"; }
	protected Command clone(Command command) { return new Set(command); }	
}
