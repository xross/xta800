package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;

public class Print extends CommandContainer {
	static private Command[] m_options = { 
		new Summary(), 
		new Structure(),
		new Asm(), 
		new Src(), 
		new Mix(), 
		new Trace(),		
		new CompletedPaths(), 
		new CompletedLoops(), 
		new CompleteBlocks(), 
		new CompletedInstructions(), 
		new UnresolvedPaths(), 
		new UnresolvedLoops(), 
		new UnresolvedBlocks(), 
		new UnresolvedInstructions(), 
		new RouteInfo(), 
		new PathInfo(), 
		new Functions(), 
		new Sources(), 		
	};
	
	public Print() { super("print"); }
	public Print(Command command) { super("print", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Display analysis information"; }
	protected Command clone(Command command) { return new Print(command); }	
}
