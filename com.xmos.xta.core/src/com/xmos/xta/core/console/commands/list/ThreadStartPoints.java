package com.xmos.xta.core.console.commands.list;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class ThreadStartPoints extends Command {
	private static final String[] COMMAND_NAMES = { "taskstartpoints", "threadstartpoints" };

	public ThreadStartPoints() {
		super(COMMAND_NAMES);
	}

	public int numArgs() {
        return 0;
	}

	public String commandLine() {
   		return name();
	}

	public String description(Engine engine, boolean detailed) {
        return name() + ": Lists the task start points";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

  		return new ThreadStartPoints();
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

   		listThreadStartPoints(engine, console);
	}

	private void listThreadStartPoints(final Engine engine, final Console console) {
		for (CoreId activeCore : engine.getActiveCores()) {			
			if (engine.getThreadStartPoints(activeCore).size() > 0) {
				console.display(String.format("%s, ", activeCore.toString()));
				for (long pc : engine.getThreadStartPoints(activeCore)) {
				    console.display(String.format("0x%x ", pc));
				}					
			}
		    console.display("\n");			
		}
	}
}
