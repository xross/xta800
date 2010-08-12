package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Exclusion extends Command {
	private static final String[] COMMAND_NAMES = { "exclusion" };

	private String m_reference;

	public Exclusion() {
		super(COMMAND_NAMES);
	}

	public Exclusion(String reference) {
		super(COMMAND_NAMES);
		m_reference = reference;
	}

    public int numArgs() {
   		return 1;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}

    public String commandLine() {
   		return name() + " " + m_reference;
    }

    public String description(Engine engine, boolean detailed) {
    	return name() + " <ANY>: Adds the given reference to the list of exclusions";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}
    	return new Exclusion(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}
		engine.addExclusion(getSrc(), m_reference);
    }
}
