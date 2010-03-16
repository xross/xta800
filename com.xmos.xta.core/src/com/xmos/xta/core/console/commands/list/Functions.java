package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Functions extends Command {
	private static final String[] COMMAND_NAMES = { "functions" };

	public Functions() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Lists the functions in the loaded application";
    }

    public Command clone(String[] args, int index) {
        return new Functions();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console)) 
			return;

		Collection<String> functionNames = engine.getNonSystemFunctionNames();
		if (functionNames.size() > 0) {
			for (String functionName : functionNames) {
				console.display(String.format("- %s\n", functionName));
			}
			console.display("\n");
		}
    }
}
