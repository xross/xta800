package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;

public class Unknowns extends Command {
	private int m_routeId;

	public Unknowns() {
	}

	public Unknowns(int routeId) {
		m_routeId = routeId;
	}

    public int numArgs() {
   		return 1;
    }

    public String name() {
    	return "unknowns";
    }

    public String commandLine() {
   		return "unknowns " + getRouteId(m_routeId);
    }

    public String description(Engine engine, boolean detailed) {
        return "unknowns <route id>: Displays the list of unknowns for the given route";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	try {
    	    return new Unknowns(getRouteId(args[index + 1]));
	    } catch (NumberFormatException e) {
	        return null;
	    }
    }

    public void execute(Shell shell, Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		if (!validateRouteId(engine, console, m_routeId))
		    return;

	    boolean found = false;
	    for (Route route : engine.getRoute(m_routeId)) {
		    Collection<Unknown> unknowns = route.getRouteUnknowns().getUnknowns(State.UNKNOWN);
		    if (unknowns.size() > 0) {
				for (Unknown unknown : unknowns) {
				    console.display(String.format("%s: %s, pc: 0x%x, backtrace: %s\n",
				    		unknown.getLevel().toString(),
				    		unknown.getDescription(),
				    		unknown.getNode().getStartPc(),
				    		unknown.getNode().getBackTrace("")));
				}
				found = true;
		    }
	    }
	    if (!found) {
		    console.display("none\n");
	    }
    }
}
