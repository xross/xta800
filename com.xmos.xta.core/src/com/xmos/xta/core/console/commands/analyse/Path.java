package com.xmos.xta.core.console.commands.analyse;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Path extends Command {
	private String m_fromRef;
	private String m_toRef;

	public Path() {
		m_fromRef = "";
		m_toRef = "";
	}

	public Path(String fromRef, String toRef) {
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

    public String name() {
    	return "path";
    }

    public String commandLine() {
    	if (hasEndPoints()) {
        	return "path " + m_fromRef + " " + m_toRef;
    	} else {
    		return "path";
    	}
    }

    public String description(Engine engine, boolean detailed) {
        return "path <from ENDPOINT_REF> <to ENDPOINT_REF>: Analyse the given path";
    }

    public Command clone(String[] args, int index) {
		if (areArgsDone(args, index + 1)) {
			return new Path();

		} else {
			if ((index + 2) >= args.length)
				return null;

			String fromRef = args[index + 1];
			String toRef = args[index + 2];
			return new Path(fromRef, toRef);
		}
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		if (hasEndPoints()) {
			boolean ok = engine.setFromEndPoint(getSrc(), m_fromRef);
			if (!ok) {
				return;
			}

			ok = engine.setToEndPoint(getSrc(), m_toRef);
			if (!ok) {
				return;
			}
		}
		engine.analysePath(getSrc());
    }

	private boolean hasEndPoints() {
    	if ((m_fromRef.length() > 0) || (m_toRef.length() > 0)) {
    		return true;
    	} else {
    		return false;
    	}
	}
}
