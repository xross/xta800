package com.xmos.xta.core.console.commands.scripter;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.scripter.Scripter;

public class Enable extends Command {
	private static final String[] COMMAND_NAMES = { "enable" };

	private String m_reference;

	public Enable() {
		super(COMMAND_NAMES);
	}

	public Enable(String reference) {
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
        return name() + ": Enable a mapping";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

        return new Enable(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Scripter scripter = engine.getScripter();
		scripter.createRefs();
		scripter.enable(getSrc(), engine, m_reference);
    }
}

