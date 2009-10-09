package com.xmos.xta.core.console.commands.dumpscript;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Scripter;

public class DumpScript extends Command {
	public DumpScript() {
	}
	
    public int numArgs() {
    	return 0;
    }        

    public String name() {
    	return "dumpscript";
    }        

    public String commandLine() {
    	return String.format("dumpscript");
    }

    public String description(Engine engine, boolean detailed) {
        return "dumpscript: Dump script which represents the current state";      	
    }

    public Command clone(String[] args, int index) {
        return new DumpScript();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Scripter scripter = new Scripter(engine);
		console.display(scripter.createScript());
    }
}

