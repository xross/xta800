package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class List extends CommandContainer {
	public enum Operation {
		ADD, REMOVE, LIST
	};
	
	static private Command[] m_options = { 
		new Labels(),
		new SrcLabels(),
		new Endpoints(),
		new Calls(),
		new AllSrcLabels(),
		new AllEndpoints(),
		new AllCalls(),
		new ThreadStartPoints(),		
		new ThreadStopPoints(),		
		new SrcCommands(),		
		new SrcLoops(),		
		new Unknowns(),
		new Functions(),
		new Sources(),
		new Knowns(),
		new Branch(Operation.LIST),
		new Exclusion(Operation.LIST),		
		new Loop(Operation.LIST),
		new LoopPath(Operation.LIST),
		new LoopScope(Operation.LIST), 
		new InstructionTime(Operation.LIST), 
		new FunctionTime(Operation.LIST), 
		new PathTime(Operation.LIST), 		
	};
	
	public List() { super("list"); }
	public List(Command command) { super("list", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Lists the given set of items"; }
	protected Command clone(Command command) { return new List(command); }		
}
