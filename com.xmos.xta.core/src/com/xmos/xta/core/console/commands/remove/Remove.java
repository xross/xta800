package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;
import com.xmos.xta.core.console.commands.list.Branch;
import com.xmos.xta.core.console.commands.list.Exclusion;

public class Remove extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(Branch.Operation.REMOVE),		
		new Exclusion(Exclusion.Operation.REMOVE),		
		new Route(),	
	};
	
	public Remove() { super("remove"); }
	public Remove(Command command) { super("remove", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Remove item from given list"; }
	protected Command clone(Command command) { return new Remove(command); }	
}
