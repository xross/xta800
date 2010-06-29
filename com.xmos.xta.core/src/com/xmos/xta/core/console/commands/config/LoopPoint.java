package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class LoopPoint extends Command {
	private static final String[] COMMAND_NAMES = { "looppoint" };

	private String m_ref;

	public LoopPoint() {
		super(COMMAND_NAMES);
		m_ref = "";
	}

	public LoopPoint(String ref) {
		super(COMMAND_NAMES);
		m_ref = ref;
	}

    public int numArgs() {
   		return 1;
    }

    public String commandLine() {
       	return name() + String.format(" %s", m_ref);
    }

    public String description(Engine engine, boolean detailed) {
       	return name() + " <ANY>: Sets the loop point";
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length)
			return null;

	    return new LoopPoint(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        engine.getLoopPoints().setLoop(getSrc(), m_ref);
    }
}
