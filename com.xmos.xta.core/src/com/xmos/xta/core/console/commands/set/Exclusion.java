package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class Exclusion extends Command {
	private static final String[] COMMAND_NAMES = { "exclusion" };
	private int m_routeId;
	private String m_refString;

	public Exclusion() {
		super(COMMAND_NAMES);
		m_routeId = 0;
		m_refString = "";
	}

	public Exclusion(int routeId, String refString) {
		super(COMMAND_NAMES);
		m_routeId = routeId;
		m_refString = refString;
	}

    public int numArgs() {
   		return 2;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.ALLREFS};
	}

    public String commandLine() {
       	return name() + String.format(" %s %s", getRouteId(m_routeId), m_refString);
    }

    public String description(Engine engine, boolean detailed) {
        String descString = name() + " <route id> <ANY>: Sets an exclusion on the given reference";
       	return descString;
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length)
			return null;

    	int routeId = 0;
   		try {
     		routeId = getRouteId(args[index + 1]);
    	} catch (NumberFormatException e) {
    		return null;
    	}
    	String ref = args[index + 2];
	    return new Exclusion(routeId, ref);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		if (!validateRouteId(engine, console, m_routeId))
		    return;
		
		Node node = getReferencedNode(engine, m_routeId, m_refString);
		if (node != null) {
			engine.setSelectedRouteId(getSrc(), m_routeId);
   		    engine.setExclusion(getSrc(), node);

		} else {
   		    engine.setExclusion(getSrc(), m_routeId, m_refString);
		}        
    }
}
