package com.xmos.xta.core.console.commands.analyse;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Function extends Command {
	private String m_functionName;

	public Function() {
		m_functionName = "";
	}

	public Function(String functionName) {
		m_functionName = functionName;
	}

    public int numArgs() {
    	return 1;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.FUNCTION};
	}

    public String name() {
    	return "function";
    }

    public String commandLine() {
    	return "function " + m_functionName;
    }

    public String description(Engine engine, boolean detailed) {
        return "function <FUNCTION_REF>: Analyse the given function";
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

        engine.analyseFunction(getSrc(), m_functionName);
    }
}
