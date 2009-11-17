package com.xmos.xta.core.console.commands.remove;

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

public class Remove extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(Operation.REMOVE),
		new Exclusion(Operation.REMOVE),
		new FunctionTime(Operation.REMOVE),
		new InstructionTime(Operation.REMOVE),
		new Loop(Operation.REMOVE),
		new LoopPath(Operation.REMOVE),
		new LoopScope(Operation.REMOVE),
		new PathTime(Operation.REMOVE),
		new Route(),
	};
	
	public Remove() { super("remove"); }
	public Remove(Command command) { super("remove", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Remove item from given list"; }
	protected Command clone(Command command) { return new Remove(command); }	
}
