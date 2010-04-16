package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class ListRouteReferences extends Command {
	private static final String[] COMMAND_NAMES = { "listroutereferences" };

	private int m_routeId;
	private String m_refString;

	public ListRouteReferences() {
		super(COMMAND_NAMES);
		m_routeId = 0;
		m_refString = "";
	}
	
	public ListRouteReferences(int routeId, String refString) {
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
    	return name() + " " + getRouteId(m_routeId) + " " + m_refString;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <route id> <ANY>: list all the matching references for the given reference on the given route";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

        try {
            return new ListRouteReferences(getRouteId(args[index + 1]), args[index + 2]);
        } catch (NumberFormatException e) {
          	return null;
        }
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (String reference : engine.getDebugger().getRouteReferences(getSrc(), m_routeId, m_refString)) {
			console.display(reference + "\n");
        }
    }
}
