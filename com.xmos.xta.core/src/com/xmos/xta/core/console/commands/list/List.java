package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class List extends CommandContainer {

	static private Command[] m_options = {
		new AllCalls(),
		new AllEndpoints(),
		new Branches(),
		new Calls(),
		new Cores(),
		new Endpoints(),
		new Exclusions(),
		new Functions(),
		new FunctionTimes(),
		new InstructionTimes(),
		new Knowns(),
		new Labels(),
		new Loops(),
		new LoopPath(),
		new LoopScopes(),
		new PathTimes(),
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
