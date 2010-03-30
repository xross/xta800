package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class DumpControlFlow extends Command {
	private static final String[] COMMAND_NAMES = { "dumpcontrolflow" };

	private String m_functionName;

	public DumpControlFlow() {
		super(COMMAND_NAMES);
		m_functionName = "";
	}

	public DumpControlFlow(String functionName) {
		super(COMMAND_NAMES);
		m_functionName = functionName;
	}

    public int numArgs() {
    	return 1;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.FUNCTION};
	}

    public String commandLine() {
    	return name() + " " + m_functionName;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <FUNCTION_REF>: Dump the control flow (dot) graph for the given function";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	String functionName = args[index + 1];
        return new DumpControlFlow(functionName);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        engine.getDebugger().dumpControlFlow(getSrc(), m_functionName, console);
    }
}
