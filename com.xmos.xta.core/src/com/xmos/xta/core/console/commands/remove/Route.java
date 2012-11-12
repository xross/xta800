package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Route extends Command {
	private static final String[] COMMAND_NAMES = { "route" };

	private int m_routeId;

	public Route() {
		super(COMMAND_NAMES);
		m_routeId = 0;
	}

	public Route(int routeId) {
		super(COMMAND_NAMES);
		m_routeId = routeId;
	}

    public int numArgs() {
    	return 1;
    }

    public String commandLine() {
    	return name() + " " + getRouteId(m_routeId);
    }

    public String description(Engine engine, boolean detailed) {
    	return name() + " <route id>: Removes the route with the given id from the current analysis";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	try {
    		return new Route(getRouteId(args[index + 1]));
    	} catch (NumberFormatException e) {
    	    return null;
    	}
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (isExecutableLoaded(engine, console) && 
		    validateRouteId(engine, console, m_routeId)) {
			engine.removeRoute(getSrc(), m_routeId);
		}
    }
}
