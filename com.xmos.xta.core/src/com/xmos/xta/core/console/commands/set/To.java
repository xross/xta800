package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class To extends Command {
	private static final String[] COMMAND_NAMES = { "to" };

	private String m_toRef;

	public To() {
		super(COMMAND_NAMES);
		m_toRef = "";
	}

	public To(String fromRef) {
		super(COMMAND_NAMES);
		m_toRef = fromRef;
	}

    public int numArgs() {
   		return 1;
    }

    public String commandLine() {
       	return name() + String.format(" %s", m_toRef);
    }

    public String description(Engine engine, boolean detailed) {
       	return name() + " <ENDPOINT_REF>: Sets the to endpoint";
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length)
			return null;

	    return new To(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

        engine.setToEndPoint(getSrc(), m_toRef);
    }
}
