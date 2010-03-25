package com.xmos.xta.core.console.commands.print;

import com.xmos.libs.util.Conversion;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming.Level;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;

public class Summary extends Command {
	private static final String[] COMMAND_NAMES = { "summary" };

	public Summary() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
    	return name() + ": Show routes summary (verbosity -2|-1|0)";
    }

    public Command clone(String[] args, int index) {
   		return new Summary();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		if (!validateRoutes(engine, console))
			return;

		for (Route route : engine.getRoutes()) {
		    displayRouteSummary(console, route, engine.getVerbosity());
		}
		displayOverallSummary(console, engine);
    }

    private void displayRouteSummary(Console console, Route route, int verbosity) {
    	if (verbosity >= 0) {
			displayRouteSummary(console, route, true, true);

    	} else if (verbosity == -1) {
			displayRouteSummary(console, route, true, false);
		}
    }

	private void displayRouteSummary(Console console, Route route,
			boolean displayFailures, boolean displayPasses) {

		RouteTiming routeTiming = route.getRouteTiming();
		if (routeTiming.isTimingSpecified()) {
			if (routeTiming.passed()) {
				if (displayPasses) {
					console.display(String.format("Route(%d) %s\n%s\n",
							route.getRouteId(),
							route.getRouteCoreContainer().getRouteContainer().getDescription(),
							routeTiming.getDescription(Level.DETAILED)));
				}
			} else {
				if (displayFailures) {
					console.display(String.format("Route(%d) %s\n%s\n",
							route.getRouteId(),
							route.getRouteCoreContainer().getRouteContainer().getDescription(),
							routeTiming.getDescription(Level.DETAILED)));
				}
			}
		} else {
			console.display(String.format("Route(%d) %s\n%s\n",
					route.getRouteId(),
					route.getRouteCoreContainer().getRouteContainer().getDescription(),
			        routeTiming.getDescription(Level.SIMPLE)));
		}
	}

    private void displayOverallSummary(Console console, Engine engine) {
		double maxViolationTime = 0.0;
		Status maxViolationStatus = new Status();

		int numFails = 0;
		int numUnspecified = 0;
		int total = 0;
		int minFrequency = 0;

		for (Route route : engine.getRoutes()) {
			RouteTiming routeTiming = route.getRouteTiming();

			if (routeTiming.isTimingSpecified()) {
				if (routeTiming.passed()) {
					Status status = new Status();
					int frequency = routeTiming.getMinFrequency(status);
					if ((status.isComplete() || status.isZero()) && (frequency > minFrequency)) {
						minFrequency = frequency;
					}
				} else {
					Status status = new Status();
					double violationTime = routeTiming.getViolationTime(status);
					if ((status.isComplete() || status.isZero()) && (violationTime > maxViolationTime)) {
						maxViolationTime = violationTime;
					}
					maxViolationStatus.mergeStatus(status.getStatus());
					++numFails;
				}
			} else {
				++numUnspecified;
			}
			++total;
		}

		if (numUnspecified == 0) {
			if (numFails > 0) {
				if (maxViolationStatus.isComplete() || maxViolationStatus.isZero()) {
				    console.display(String.format("Fail, %d/%d, Max Violation: %s\n",
				    		numFails, total, Conversion.getTimeString(maxViolationTime)));
				} else {
				    console.display(String.format("Fail, %d/%d, Max Violation: %s\n",
				    		numFails, total, maxViolationStatus.getString()));
				}
			} else {
				console.display(String.format("Pass, Min Core Frequency: %d MHz\n",
			    		minFrequency));
			}
		} else {
		    console.display("Fail, Timing Unspecified\n");
		}
    }
}
