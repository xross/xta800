package com.xmos.xta.core.console.commands.scripter;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.scripter.Scripter;

public class Dump extends Command {
	public Dump() {
	}
	
    public int numArgs() {
    	return 0;
    }        

    public String name() {
    	return "dump";
    }        

    public String commandLine() {
    	return "dump";
    }

    public String description(Engine engine, boolean detailed) {
        return "dump: Dump script which represents the current state";      	
    }

    public Command clone(String[] args, int index) {
        return new Dump();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Scripter scripter = engine.getScripter();
		scripter.createRefs();
		console.display(scripter.dump());
    }
}

