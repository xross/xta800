package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class MemUsage extends Command {
	private static final String[] COMMAND_NAMES = { "memusage" };

	public MemUsage() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Display the current memory usage for the jvm";
    }

    public Command clone(String[] args, int index) {
        return new MemUsage();
    }

    public void execute(Shell shell, Engine engine, Console console) {    	
		console.display(String.format("Memory Usage [max/total/free]: %d/%d/%d\n",
			Runtime.getRuntime().maxMemory(),
			Runtime.getRuntime().totalMemory(),
			Runtime.getRuntime().freeMemory()));
    }
}
