package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class To extends Command {
	private String m_toRef;

	public To() {
		m_toRef = "";
	}

	public To(String fromRef) {
		m_toRef = fromRef;
	}

    public int numArgs() {
   		return 1;
    }

    public String name() {
    	return "to";
    }

    public String commandLine() {
       	return String.format("to %s", m_toRef);
    }

    public String description(Engine engine, boolean detailed) {
       	return "to <ENDPOINT_REF>: Sets the to endpoint";
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
