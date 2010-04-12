package com.xmos.xta.core.console.commands.analyze;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Endpoints extends Command {
	private static final String[] COMMAND_NAMES = { "endpoints", "path" };

	private String m_fromRef;
	private String m_toRef;

	public Endpoints() {
		super(COMMAND_NAMES);
		m_fromRef = "";
		m_toRef = "";
	}

	public Endpoints(String fromRef, String toRef) {
		super(COMMAND_NAMES);
		m_fromRef = fromRef;
		m_toRef = toRef;
	}

    public int numArgs() {
    	if (hasEndPoints()) {
    		return 2;
    	} else {
    		return 0;
    	}
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ENDPOINT, ArgType.ENDPOINT};
	}

    public String commandLine() {
    	if (hasEndPoints()) {
        	return name() + " " + m_fromRef + " " + m_toRef;
    	} else {
    		return name();
    	}
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <from ENDPOINT_REF> <to ENDPOINT_REF>: Analyze between the specified endpoints";
    }

    public Command clone(String[] args, int index) {
		if (areArgsDone(args, index + 1)) {
			return new Endpoints();

		} else {
			if ((index + 2) >= args.length)
				return null;

			String fromRef = args[index + 1];
			String toRef = args[index + 2];
			return new Endpoints(fromRef, toRef);
		}
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		if (hasEndPoints()) {
			boolean fromOk = engine.getPathPoints().setFrom(getSrc(), m_fromRef);
			boolean toOk = engine.getPathPoints().setTo(getSrc(), m_toRef);
					
			if (!fromOk || !toOk)
				return;
		}
		engine.analyzePath(getSrc());
    }

	private boolean hasEndPoints() {
    	if ((m_fromRef.length() > 0) && (m_toRef.length() > 0)) {
    		return true;
    	} else {
    		return false;
    	}
	}
}
