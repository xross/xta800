package com.xmos.xta.console.commands.unset;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;
import com.xmos.xta.console.commands.set.Exclusion;
import com.xmos.xta.console.commands.set.Loop;
import com.xmos.xta.console.commands.set.LoopPath;
import com.xmos.xta.console.commands.set.Required;
import com.xmos.xta.console.commands.set.Time;

public class Unset extends CommandContainer {
	static private Command[] m_options = { 
		new Loop(Loop.Operation.UNSET),
		new LoopPath(LoopPath.Operation.UNSET),
		new Time(Time.Operation.UNSET), 
		new Required(Required.Operation.UNSET), 
		new Exclusion(Exclusion.Operation.UNSET),
	};
	
	public Unset() { super("unset"); }
	public Unset(Command command) { super("unset", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Unset item from given list"; }
	protected Command clone(Command command) { return new Unset(command); }	
}
