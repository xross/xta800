package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;

public class Summary extends CommandContainer {
	static private Command[] m_options = { 
		new Simple(), 
		new Complete(), 
		new CompleteLoops(), 
		new CompleteBlocks(), 
		new CompleteInstructions(), 
		new Unresolved(), 
		new UnresolvedLoops(), 
		new UnresolvedBlocks(), 
		new UnresolvedInstructions(), 
		new Functions(), 
		new Sources(), 
		new Info(), 
	};
	
	public Summary() { super("summary"); }
	public Summary(Command command) { super("summary", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Display timing summaries"; }
	protected Command clone(Command command) { return new Summary(command); }
}
