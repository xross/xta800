package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class ThreadStopPoints extends Command {
	private static final String[] COMMAND_NAMES = { "threadstoppoints" };

	public ThreadStopPoints() {
		super(COMMAND_NAMES);
	}

	public int numArgs() {
        return 0;
	}

	public String commandLine() {
   		return name();
	}

	public String description(Engine engine, boolean detailed) {
        return name() + ": Lists the thread stop points";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

   		return new ThreadStopPoints();
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

   		listThreadStopPoints(engine, console);
	}

	private void listThreadStopPoints(final Engine engine, final Console console) {
		for (NodeCorePair activeCore : engine.getActiveCores()) {			
			if (engine.getThreadStopPoints(activeCore.getNode(), activeCore.getCore()).size() > 0) {
				console.display(String.format("node: %d, core: %d, ", activeCore.getNode(), activeCore.getCore()));
				for (long pc : engine.getThreadStopPoints(activeCore.getNode(), activeCore.getCore())) {
		    	    console.display(String.format("0x%x ", pc));
		    	}					
			}				
		    console.display("\n");				
		}
	}
}
