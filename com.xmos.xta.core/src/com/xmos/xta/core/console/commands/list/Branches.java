package com.xmos.xta.core.console.commands.list;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.core.engine.utils.CoreId;

public class Branches extends Command {
	private static final String[] COMMAND_NAMES = { "branches" };

	private int m_routeId;
	private boolean m_routeIdSet;

	public Branches() {
		super(COMMAND_NAMES);
		m_routeId = 0;
		m_routeIdSet = false;
	}

	public Branches(int routeId) {
		super(COMMAND_NAMES);
		m_routeId = routeId;
		m_routeIdSet = true;
	}

	public int numArgs() {
    	if (m_routeIdSet) {
    		return 1;
    	}
    	return 0;
	}

	public ArgType[] getArgTypes() {
        return super.getArgTypes();
	}

	public String commandLine() {
    	if (m_routeIdSet) {
        	return name() + " " + getRouteId(m_routeId);
    	}
   		return name();
	}

	public String description(Engine engine, boolean detailed) {
        return name() + " [route id]: Lists the branches - optionally for the specified route";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

		if (!areArgsDone(args, index + 1)) {
	    	try {
	    		return new Branches(getRouteId(args[index + 1]));
		    } catch (NumberFormatException e) {
		    	return null;
		    }
		}

		return new Branches();
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		if (m_routeIdSet) {
			listBranches(engine, console, engine.getRoute(m_routeId));
		} else {
			listBranches(engine, console);
		}
	}

    private void listBranches(Engine engine, Console console, Collection<Route> routes) {
    	if (routes.isEmpty()) {
    		return;
    	}
    	Route firstRoute = routes.iterator().next();
    	RouteContainer routeContainer = firstRoute.getRouteCoreContainer().getRouteContainer();

    	Map<Reference, java.util.List<Reference>> branches = routeContainer.getBranches();
		for (Reference fromRef : branches.keySet()) {
			String to = getTo(branches, fromRef);
			if (to.length() > 0) {
	    	    console.display(String.format("- %s =>%s\n", fromRef.toString(), to));
			}
		}
	}

	private String getTo(Map<Reference, java.util.List<Reference>> branches, Reference fromRef) {
		StringBuffer result = new StringBuffer();
		for (Reference ref : branches.get(fromRef)) {
			result.append(String.format(" %s", ref.toString()));
		}
		return result.toString();
	}

	private void listBranches(Engine engine, Console console) {
		for (CoreId activeCore : engine.getActiveCores()) {			
			Map<Reference, Set<BranchInfo>> branches = engine.getBranches(activeCore);
			for (Reference fromRef : branches.keySet()) {
				String to = getTo(branches, fromRef, false);
				if (to.length() > 0) {
		    	    console.display(String.format("- %s =>%s (binary)\n", fromRef.toString(), to));
				}
				to = getTo(branches, fromRef, true);
				if (to.length() > 0) {
		    	    console.display(String.format("- %s =>%s (user)\n", fromRef.toString(), to));
				}
			}
		}
	}

	private String getTo(Map<Reference, Set<BranchInfo>> branches, Reference fromRef, boolean isUser) {
		StringBuffer result = new StringBuffer();
		for (BranchInfo info : branches.get(fromRef)) {
			if (isUser == info.isUser()) {
				result.append(String.format(" %s", info.getRef().toString()));
			}
		}
		return result.toString();
	}
}
