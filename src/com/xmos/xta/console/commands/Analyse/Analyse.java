package com.xmos.xta.console.commands.Analyse;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.CommandContainer;

public class Analyse extends CommandContainer {
	static private Command[] m_options = { 
		new Path(), 
		new Function(), 
	};
	
	public Analyse() { super("analyse"); }
	public Analyse(Command command) { super("analyse", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Perform timing analysis"; }
	protected Command clone(Command command) { return new Analyse(command); }	
}
