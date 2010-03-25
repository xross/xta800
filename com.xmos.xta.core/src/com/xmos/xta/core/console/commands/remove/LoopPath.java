package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class LoopPath extends Command {
	private static final String[] COMMAND_NAMES = { "looppath" };

	private String m_refString;

	public LoopPath() {
		super(COMMAND_NAMES);
	}

	public LoopPath(String refString) {
		super(COMMAND_NAMES);
		m_refString = refString;
	}

    public int numArgs() {
   		return 1;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}

    public String commandLine() {
       	return name() + " " + " " + m_refString;
    }

    public String description(Engine engine, boolean detailed) {
		return name() + " <ANY_REF|*>: Removes the given loop path count define to the list of defines";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

    	return new LoopPath(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (isExecutableLoaded(engine, console)) {
			engine.removeLoopPath(getSrc(), m_refString);
		}
    }
}
