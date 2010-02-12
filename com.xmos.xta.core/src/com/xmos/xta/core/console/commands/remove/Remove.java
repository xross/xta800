package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;
import com.xmos.xta.core.console.commands.remove.Branch;
import com.xmos.xta.core.console.commands.remove.Exclusion;
import com.xmos.xta.core.console.commands.remove.FunctionTime;
import com.xmos.xta.core.console.commands.remove.InstructionTime;
import com.xmos.xta.core.console.commands.remove.Loop;
import com.xmos.xta.core.console.commands.remove.LoopPath;
import com.xmos.xta.core.console.commands.remove.LoopScope;
import com.xmos.xta.core.console.commands.remove.PathTime;

public class Remove extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(),
		new Exclusion(),
		new From(),
		new FunctionTime(),
		new InstructionTime(),
		new Loop(),
		new LoopPath(),
		new LoopScope(),
		new PathTime(),
		new Route(),
		new To(),
	};
	
	public Remove() { super("remove"); }
	public Remove(Command command) { super("remove", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Remove item from given list"; }
	protected Command clone(Command command) { return new Remove(command); }	
}
