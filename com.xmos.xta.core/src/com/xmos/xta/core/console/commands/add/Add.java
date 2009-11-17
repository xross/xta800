package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;
import com.xmos.xta.core.console.commands.list.Branch;
import com.xmos.xta.core.console.commands.list.Exclusion;
import com.xmos.xta.core.console.commands.list.FunctionTime;
import com.xmos.xta.core.console.commands.list.InstructionTime;
import com.xmos.xta.core.console.commands.list.Loop;
import com.xmos.xta.core.console.commands.list.LoopPath;
import com.xmos.xta.core.console.commands.list.LoopScope;
import com.xmos.xta.core.console.commands.list.PathTime;
import com.xmos.xta.core.console.commands.list.List.Operation;

public class Add extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(Operation.ADD),
		new Exclusion(Operation.ADD),	
		new FunctionTime(Operation.ADD), 
		new InstructionTime(Operation.ADD), 
		new Loop(Operation.ADD),
		new LoopPath(Operation.ADD),
		new LoopScope(Operation.ADD), 
		new PathTime(Operation.ADD), 				
	};
	
	public Add() { super("add"); }
	public Add(Command command) { super("add", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Add item to given list"; }
	protected Command clone(Command command) { return new Add(command); }	
}
