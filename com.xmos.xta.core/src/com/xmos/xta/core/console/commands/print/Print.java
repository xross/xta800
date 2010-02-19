package com.xmos.xta.core.console.commands.print;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;

public class Print extends CommandContainer {
	static private Command[] m_options = {
		new Summary(),
		new Structure(),
		new Asm(),
		new Src(),
		new Mix(),
		new Trace(),
		new RouteInfo(),
		new NodeInfo(),
		new Warnings(),
		new TimeDistribution(),
	};

	public Print() { super("print"); }
	public Print(Command command) { super("print", command); }

	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Display analysis information"; }
	protected Command clone(Command command) { return new Print(command); }
}
