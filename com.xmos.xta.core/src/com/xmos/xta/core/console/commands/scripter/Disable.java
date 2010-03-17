package com.xmos.xta.core.console.commands.scripter;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.scripter.Scripter;

public class Disable extends Command {
	private static final String[] COMMAND_NAMES = { "disable" };

	private String m_reference;

	public Disable() {
		super(COMMAND_NAMES);
	}

	public Disable(String reference) {
		super(COMMAND_NAMES);
		m_reference = reference;
	}

    public int numArgs() {
    	return 1;
    }

    public String commandLine() {
    	return name() + " " + m_reference;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Disable a mapping";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

        return new Disable(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Scripter scripter = engine.getScripter();
		scripter.createRefs();
		scripter.disable(getSrc(), engine, m_reference);
    }
}

