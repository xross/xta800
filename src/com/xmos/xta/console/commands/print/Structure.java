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
    private int m_verbosity;
    private boolean m_verbositySpecified;
	
	public Structure() {
		m_mode = Mode.GLOBAL;
	}

	public Structure(Mode mode, int routeId, int verbosity, boolean verbositySpecified) {
		m_mode = mode;
		m_routeId = routeId;
		m_verbosity = verbosity;
		m_verbositySpecified = verbositySpecified;
	}

    public int numArgs() {
    	switch (m_mode) {
    	case GLOBAL:
    		return m_verbositySpecified ? 1 : 0;
    	case ROUTE:
    		return m_verbositySpecified ? 2 : 1;
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
    		return m_verbositySpecified ? 
       			"structure " + m_verbosity :
    			"structure";
    	case ROUTE:
    		return m_verbositySpecified ? 
       			"structure " + getRouteId(m_routeId) + " " + m_verbosity :
    			"structure " + getRouteId(m_routeId);
    	}
		assert(false);
	    return "";    	
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "structure [route id] [verbosity 0|1]: Displays the structure for the program, or if sepcified, just the given route";
    	return descString;    	                
    }

    public Command clone(String[] args, int index) {    
    	int routeId = 0;
    	boolean routeIdSpecified = false;
		if (!areArgsDone(args, index + 1)) {
	    	try {
	    		routeId = getRouteId(args[index + 1]);	
	    		
		    } catch (NumberFormatException e) {
		        return null;
		    }    	
		    routeIdSpecified = true;
		}
		
		int verbosity = 0;
		boolean verbositySpecified = false;
    	if (!areArgsDone(args, index + 2)) {
        	try {
        		verbosity = Integer.parseInt(args[index + 2]); 
        		if ((verbosity < 0) || (verbosity > 1))
        			return null;

        	} catch (NumberFormatException e) {
    	        return null;
    	    }
        	verbositySpecified = true;
    	}		
    	
    	if (routeIdSpecified) { 
    	    return new Structure(Mode.ROUTE, routeId, verbosity, verbositySpecified);
    	}    	
    	return new Structure(Mode.GLOBAL, 0, verbosity, verbositySpecified);
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
	    	nodes = engine.getRoute(m_routeId).getSubRoutes();
		    if (!validateRouteId(engine, console, m_routeId))
			    return;
	    	break;	    	
	    }
	    
		StructureDisplayer displayer = new StructureDisplayer(engine, console, m_verbosity, m_mode, m_routeId);	
		displayer.display(nodes);
    }
}
