package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class From extends Command {
	private static final String[] COMMAND_NAMES = { "from" };

	private String m_fromRef;

	public From() {
		super(COMMAND_NAMES);
		m_fromRef = "";
	}

	public From(String fromRef) {
		super(COMMAND_NAMES);
		m_fromRef = fromRef;
	}

    public int numArgs() {
   		return 1;
    }

    public String commandLine() {
       	return name() + String.format(" %s", m_fromRef);
    }

    public String description(Engine engine, boolean detailed) {
       	return name() + " <ENDPOINT_REF>: Sets the from endpoint";
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length)
			return null;

	    return new From(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        engine.getPathPoints().setFrom(getSrc(), m_fromRef);
    }
}
