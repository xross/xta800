package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class List extends CommandContainer {
	public enum Operation {
		ADD, REMOVE, LIST
	};
	
	static private Command[] m_options = { 
		new Branch(Operation.LIST),
		new Calls(),
		new AllCalls(),
		new Endpoints(),
		new AllEndpoints(),
		new Exclusion(Operation.LIST),		
		new Functions(),
		new FunctionTime(Operation.LIST), 
		new InstructionTime(Operation.LIST), 
		new Knowns(),
		new Labels(),
		new Loop(Operation.LIST),
		new LoopPath(Operation.LIST),
		new LoopScope(Operation.LIST), 
		new PathTime(Operation.LIST), 		
		new Sources(),
		new SrcCommands(),		
		new SrcLoops(),		
		new SrcLabels(),
		new AllSrcLabels(),
		new ThreadStartPoints(),		
		new ThreadStopPoints(),		
		new Unknowns(),
	};
	
	public List() { super("list"); }
	public List(Command command) { super("list", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Lists the given set of items"; }
	protected Command clone(Command command) { return new List(command); }		
}
