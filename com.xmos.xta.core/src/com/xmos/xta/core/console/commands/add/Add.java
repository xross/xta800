package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;
import com.xmos.xta.core.console.commands.list.Branch;
import com.xmos.xta.core.console.commands.list.Exclusion;

public class Add extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(Branch.Operation.ADD),
		new Exclusion(Exclusion.Operation.ADD),		
	};
	
	public Add() { super("add"); }
	public Add(Command command) { super("add", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Add item to given list"; }
	protected Command clone(Command command) { return new Add(command); }	
}
