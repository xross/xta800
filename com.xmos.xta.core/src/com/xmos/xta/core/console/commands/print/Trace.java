package com.xmos.xta.core.console.commands.print;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;

public class Trace extends Command {
    private static final String[] COMMAND_NAMES = { "trace" };

    private int m_routeId;
    private int m_nodeId;
    private boolean m_nodeIdSet;

    public Trace() {
        super(COMMAND_NAMES);
        m_routeId = 0;
        m_nodeId = 0;
        m_nodeIdSet = false;
    }

    public Trace(int routeId, int nodeId, boolean nodeIdSpecified) {
        super(COMMAND_NAMES);
        m_routeId = routeId;
        m_nodeId = nodeId;
        m_nodeIdSet = nodeIdSpecified;
    }

    public int numArgs() {
        if (m_nodeIdSet) {
            return 2;
        }
        return 1;
    }

    public String commandLine() {
        if (m_nodeIdSet) {
            return name() + " " + getRouteId(m_routeId) + " " + m_nodeId;
        }
        return name() + " " + getRouteId(m_routeId);
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <route id> [node id]: Display instruction trace for the worst case path of the given route/node";
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

        if (!areArgsDone(args, index + 2)) {
            try {
                return new Trace(routeId, getNodeId(args[index + 2]), true);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return new Trace(routeId, 0, false);
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        if (!validateRouteId(engine, console, m_routeId))
            return;

        if (m_nodeIdSet && !validateNodeId(engine, console, m_routeId, m_nodeId))
            return;

        TraceDisplayer displayer = new TraceDisplayer(console);
        for (Route route : engine.getRoute(m_routeId)) {
            if (m_nodeIdSet) {
                displayer.display(route.getNode(m_nodeId), engine.getCase());
            } else {
                displayer.display(route.getNode(), engine.getCase());
            }
        }
    }
}
