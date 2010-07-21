package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class DumpBinaryInfo extends Command {
	private static final String[] COMMAND_NAMES = { "dumpbinaryinfo" };

	public DumpBinaryInfo() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {};
	}

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " Dump the binary information for the current executable";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
        return new DumpBinaryInfo();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;
        StringBuffer sb = engine.printBinaryInformation();
        console.display(sb.toString());
    }
}

