package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class Cores extends Command {
	private static final String[] COMMAND_NAMES = { "cores" };

	public Cores() {
		super(COMMAND_NAMES);
	}

	public int numArgs() {
        return 0;
	}

	public String commandLine() {
   		return name();
	}

	public String description(Engine engine, boolean detailed) {
        return name() + ": Lists the active cores";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

   		return new Cores();
	}

	public void execute(Shell shell, final Engine engine, final Console console) {
		for (NodeCorePair activeCore : engine.getActiveCores()) {			
			console.display(String.format("node: %d, core: %d\n", activeCore.getNode(), activeCore.getCore()));
		}
	}
}