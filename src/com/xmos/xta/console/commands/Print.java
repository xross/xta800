package com.xmos.xta.console.commands;

class Print extends CommandContainer {
	static private Command[] m_options = { 
		new Asm(), 
		new Src(), 
		new Mix(), 
		new Trace(), 
	};
	
	Print() { super("print"); }
	Print(Command command) { super("print", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Display path information"; }
	protected Command clone(Command command) { return new Print(command); }	
}
