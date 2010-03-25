package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class To extends Command {
	private static final String[] COMMAND_NAMES = { "to" };

	public To() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
   		return 0;
    }

    public String commandLine() {
       	return name();
    }

    public String description(Engine engine, boolean detailed) {
       	return name() + ": Removes the to endpoint";
    }

    public Command clone(String[] args, int index) {
	    return new To();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (isExecutableLoaded(engine, console)){
			 engine.removeToEndPoint(getSrc());
		}
    }
}
