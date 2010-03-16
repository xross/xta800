package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Verbosity extends Command {
	private static final String[] COMMAND_NAMES = { "verbosity" };

	private int m_verbosity;

	public Verbosity() {
		super(COMMAND_NAMES);
		m_verbosity = 0;
	}

	public Verbosity(int verbosity) {
		super(COMMAND_NAMES);
		m_verbosity = verbosity;
	}

    public int numArgs() {
    	return 1;
    }

    public String commandLine() {
    	return name() + String.format(" %d", m_verbosity);
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <level>: Sets the tool verbosity level (range: -10 -> +10, default: 0)";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	int verbosity;
        try { verbosity = Integer.parseInt(args[index + 1]); }
        catch (NumberFormatException e) {
            return null;
        }
        return new Verbosity(verbosity);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        engine.setVerbosity(m_verbosity);
    }
}
