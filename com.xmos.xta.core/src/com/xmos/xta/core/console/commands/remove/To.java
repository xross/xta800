package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class To extends Command {
	public To() {
	}

    public int numArgs() {
   		return 0;
    }

    public String name() {
    	return "to";
    }

    public String commandLine() {
       	return "to";
    }

    public String description(Engine engine, boolean detailed) {
       	return "to: Removes the to endpoint";
    }

    public Command clone(String[] args, int index) {
	    return new To();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        engine.removeToEndPoint(getSrc());
    }
}
