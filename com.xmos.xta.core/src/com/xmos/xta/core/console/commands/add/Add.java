package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;
import com.xmos.xta.core.console.commands.add.Branch;
import com.xmos.xta.core.console.commands.add.Exclusion;
import com.xmos.xta.core.console.commands.add.FunctionTime;
import com.xmos.xta.core.console.commands.add.InstructionTime;
import com.xmos.xta.core.console.commands.add.Loop;
import com.xmos.xta.core.console.commands.add.LoopPath;
import com.xmos.xta.core.console.commands.add.LoopScope;
import com.xmos.xta.core.console.commands.add.PathTime;

public class Add extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(),
		new Exclusion(),	
		new FunctionTime(), 
		new InstructionTime(), 
		new Loop(),
		new LoopPath(),
		new LoopScope(), 
		new PathTime(), 				
	};
	
	public Add() { super("add"); }
	public Add(Command command) { super("add", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Add item to given list"; }
	protected Command clone(Command command) { return new Add(command); }	
}
