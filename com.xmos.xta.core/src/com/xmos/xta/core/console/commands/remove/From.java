package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class From extends Command {
	public From() {
	}

    public int numArgs() {
   		return 0;
    }

    public String name() {
    	return "from";
    }

    public String commandLine() {
    	return "from";
    }

    public String description(Engine engine, boolean detailed) {
       	return "from: Removes the from endpoint";
    }

    public Command clone(String[] args, int index) {
	    return new From();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        engine.removeFromEndPoint(getSrc());
    }
}
