package com.xmos.xta.core.console.commands.print;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming.Level;

public class RouteInfo extends Command {
    private static final String[] COMMAND_NAMES = { "routeinfo" };

    private int m_routeId;

    public RouteInfo() {
        super(COMMAND_NAMES);
        m_routeId = 0;
    }

    public RouteInfo(int routeId) {
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
        return name() + " <route id>: Show detailed information for the given route";
    }

    public Command clone(String[] args, int index) {
        if ((index + numArgs()) >= args.length)
            return null;

        try {
            return new RouteInfo(getRouteId(args[index + 1]));
        } catch (NumberFormatException e) {
              return null;
        }
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;

        if (!validateRouteId(engine, console, m_routeId))
            return;

        for (Route route : engine.getRoute(m_routeId)) {
            RouteTiming routeTiming = route.getRouteTiming();

            console.display(String.format("%s %s\n",
                    route.parentRouteContainer().getDescription(),
                    route.getEndpointsPcsString()));

            console.display(routeTiming.getDescription(Level.COMPLETE));
        }
    }
}
