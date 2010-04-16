package com.xmos.xta.core.console.commands.analyze;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Function extends Command {
	private static final String[] COMMAND_NAMES = { "function" };

	private String m_functionName;

	public Function() {
		super(COMMAND_NAMES);
		m_functionName = "";
	}

	public Function(String functionName) {
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
        return name() + " <FUNCTION>: Analyze the given function";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	String functionName = args[index + 1];
        return new Function(functionName);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        engine.analyzeFunction(getSrc(), m_functionName);
    }
}
