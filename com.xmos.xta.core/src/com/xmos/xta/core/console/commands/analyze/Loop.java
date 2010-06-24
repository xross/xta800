package com.xmos.xta.core.console.commands.analyze;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Loop extends Command {
	private static final String[] COMMAND_NAMES = { "loop" };

	private String m_loopRef;

	public Loop() {
		super(COMMAND_NAMES);
		m_loopRef = "";
	}

	public Loop(String loopRef) {
		super(COMMAND_NAMES);
		m_loopRef = loopRef;
	}

    public int numArgs() {
    	return 1;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}

    public String commandLine() {
    	return name() + " " + m_loopRef;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <ANY>: Analyze the given loop";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	String loopRef = args[index + 1];
        return new Loop(loopRef);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        engine.analyzeLoop(getSrc(), m_loopRef);
    }
}
