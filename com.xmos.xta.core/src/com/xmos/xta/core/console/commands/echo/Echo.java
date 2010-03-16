package com.xmos.xta.core.console.commands.echo;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Echo extends Command {
	private static final String[] COMMAND_NAMES = { "echo" };

	private String m_string;

	public Echo() {
		super(COMMAND_NAMES);
	}

	public Echo(String string) {
		super(COMMAND_NAMES);
		m_string = string;
	}

    public int numArgs() {
    	return 1;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " \"text\": prints the text to the console";
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length)
			return null;

        return new Echo(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
    	console.display(m_string + "\n");
    }
}