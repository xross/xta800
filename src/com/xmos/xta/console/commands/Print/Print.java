package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.CommandContainer;

public class Print extends CommandContainer {
	static private Command[] m_options = { 
		new Asm(), 
		new Src(), 
		new Mix(), 
		new Trace(), 
	};
	
	public Print() { super("print"); }
	public Print(Command command) { super("print", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Display path information"; }
	protected Command clone(Command command) { return new Print(command); }	
}
