package com.xmos.xta.core.console.commands.list;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class Exclusions extends Command {
	private static final String[] COMMAND_NAMES = { "exclusions" };

	private int m_routeId;
	private boolean m_routeIdSet;

	public Exclusions() {
		super(COMMAND_NAMES);
		m_routeId = 0;
		m_routeIdSet = false;
	}

	public Exclusions(int routeId) {
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
    	return name() + " [route id]: Displays the exclusions - optionally for the specified route";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

		if (!areArgsDone(args, index + 1)) {
	    	try {
	    		return new Exclusions(getRouteId(args[index + 1]));
	    	} catch (NumberFormatException e) {
		    	return null;
		    }
		}

    	return new Exclusions();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		if (m_routeIdSet) {
			listExclusions(engine, console, engine.getRoute(m_routeId));
		} else {
			listExclusions(console, engine.getExclusions());
		}
    }

    private void listExclusions(Engine engine, Console console, Collection<Route> routes) {
    	Set<Reference> uniqueExclusions = new IteratableSet<Reference>();
    	
    	for (Route route : routes) {
		    Collection<Unknown> knowns = route.getRouteUnknowns().getUnknowns(State.KNOWN);
		    for (Unknown known : knowns) {
		    	if (known.getType() == Type.EXCLUSION) {
		    		for (Reference ref : known.getReferences()) {
		    			uniqueExclusions.add(ref);
		    		}
		    	}
		    }
		    
		    RouteContainer routeContainer = route.getRouteCoreContainer().getRouteContainer();
		    for (Reference ref : routeContainer.getExclusions()) {
		    	uniqueExclusions.add(ref);
		    }
    	}
    	listExclusions(console, uniqueExclusions);
	}

	private void listExclusions(Console console, Collection<Reference> exclusions) {
    	if (exclusions == null)
	    	return;

	    if (exclusions.size() > 0) {
	    	for (Reference exclusion : exclusions) {
	    		console.display(String.format("%s: %s\n",
	    	    		exclusion, exclusion.getPcString())); 
	    	}
	    } else {
	    	console.display("none\n");
	    }
    }
 }
