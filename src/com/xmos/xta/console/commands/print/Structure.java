package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class Structure extends Command {
	public enum Mode {
		GLOBAL,
		ROUTE,
	}
	private Mode m_mode;
	private int m_routeId;
	
	public Structure() {
		m_mode = Mode.GLOBAL;
	}

	public Structure(Mode mode, int routeId) {
		m_mode = mode;
		m_routeId = routeId;
	}

    public int numArgs() {
    	switch (m_mode) {
    	case GLOBAL:
    		return 0;
    	case ROUTE:
    		return 1;
    	}
		assert(false);
	    return 0;
    }        

    public String name() {
    	return "structure";
    }        

    public String commandLine() {
    	switch (m_mode) {
    	case GLOBAL:
    		return "structure";
    	case ROUTE:
    		return "structure " + getRouteId(m_routeId);
    	}
		assert(false);
	    return "";    	
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "structure : Displays the structure for the program, or if sepcified, just the given route";
    	return descString;    	                
    }

    public Command clone(String[] args, int index) {    	
		if (!areArgsDone(args, index + 1)) {
	    	try {
	    	    return new Structure(Mode.ROUTE, getRouteId(args[index + 1]));
		    } catch (NumberFormatException e) {
		        return null;
		    }    	
		}
    	return new Structure(Mode.GLOBAL, 0);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
		
	    Collection<Node> nodes = null;
	    switch (m_mode) {
	    case GLOBAL:
	    	nodes = engine.getStructureGraph().getThreads();
	    	break;
	    	
	    case ROUTE:
	    	nodes = engine.getRoute(m_routeId).getCompletedPaths();
		    if (!validateRouteId(engine, console, m_routeId))
			    return;
	    	break;	    	
	    }
	    
		StructureDisplayer displayer = new StructureDisplayer(console);	
		displayer.display(nodes);
    }
}
