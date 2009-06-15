package com.xmos.xta.console.commands.set;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;

public class Exclusion extends Command {	
	public enum Operation {
		SET, 
		UNSET,
		LIST
	};
	public enum Mode {
		GLOBAL,
		ROUTE,
	}
	private Mode m_mode;
	private int m_routeId;
	private String m_reference;
	private Operation m_operation;
	
	public Exclusion(Operation operation) {
		m_operation = operation;
		m_mode = Mode.GLOBAL;
	}

	public Exclusion(Operation operation, String reference, Mode mode, int routeId) {
		m_operation = operation;
		m_reference = reference;
		m_mode = mode;
		m_routeId = routeId;
	}
	
    public int numArgs() {
    	switch (m_operation) {
    	case SET:
    	case UNSET:
        	switch (m_mode) {
        	case GLOBAL:
        		return 1;
        	case ROUTE:
        		return 2;
        	}
    		assert(false);
    	    return 0;
        	
    	case LIST:
            return 0;
    	}    		
    	assert(false);
        return 0;
    }        

    public String name() {
    	switch (m_operation) {
    	case SET:
    	case UNSET:
    		return "exclusion"; 
    	case LIST:
    		return "exclusions";
    	}    			
    	assert(false);
        return "";    	
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case SET:
    	case UNSET:
        	switch (m_mode) {
        	case GLOBAL:
        		return "exclusion " + m_reference;
        	case ROUTE:
        		return "exclusion " + m_reference + " " + getRouteId(m_routeId);
        	}
    		assert(false);
    	    return "";
    	    
    	case LIST:
    		return "exclusions";
    	}    			
    	assert(false);
        return "";    	
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "";
    	switch (m_operation) {
    	case SET:
        	descString = "exclusion <ANY_REF> [route id]: Excludes the block containing the given reference";
        	break;
    	case UNSET:
        	descString = "exclusion: <ANY_REF> [route id]: Un-excludes the block containing the given reference";
        	break;
    	case LIST:
        	descString = "exclusions [route id]: Displays the exclusions for the given route/global tree";
        	break;
    	}
    	return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		
	    
    	switch (m_operation) {
    	case SET:    		
    	case UNSET:
    		String reference = args[index + 1];    		
    		if (!areArgsDone(args, index + 2)) {
    	    	try {
    	    	    return new Exclusion(m_operation, reference, 
    	    	    		Mode.ROUTE, getRouteId(args[index + 2]));
    	    	    
    		    } catch (NumberFormatException e) {
    		        return null;
    		    }    	
    		}
        	return new Exclusion(m_operation, reference, Mode.GLOBAL, 0);

    	case LIST:
    		if (!areArgsDone(args, index + 1)) {
    	    	try {
    	    	    return new Exclusion(m_operation, "",
    	    	    		Mode.ROUTE, getRouteId(args[index + 1]));
    	    	    
    		    } catch (NumberFormatException e) {
    		        return null;
    		    }    	
    		}
        	return new Exclusion(m_operation, "", Mode.GLOBAL, 0);
    	}
    	assert(false);
		return null;    						
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

    	switch (m_operation) {
    	case SET:
    		setExclusion(engine, console);
    		break;

    	case UNSET:
    		unsetExclusion(engine, console);
    		break;
    		
    	case LIST:
    		listExclusions(engine, console);
    		break;
    	}
    }
    
    private void setExclusion(Engine engine, Console console) {
        switch (m_mode) {
        case GLOBAL:
        	if (!engine.setGlobalExclusion(m_reference)) {
        		console.display(String.format(
        				"Error: Failed to set exclusion on reference: %s\n", 
        				m_reference));    		        		
        	}
        	break;

        case ROUTE:
    	    if (!engine.setRouteExclusion(m_reference, m_routeId)) {
        		console.display(String.format(
        				"Error: Failed to set exclusion on reference: %s\n", 
        				m_reference));
    	    }
        	break;
        }
    }

    private void unsetExclusion(Engine engine, Console console) {
        switch (m_mode) {
        case GLOBAL:
        	if (!engine.unsetGlobalExclusion(m_reference)) {
        		console.display(String.format(
        				"Error: Failed to unset exclusion on reference: %s\n", 
        				m_reference));    		        		
        	}
        	break;
        	
        case ROUTE:
    	    if (!engine.unsetRouteExclusion(m_reference, m_routeId)) {
        		console.display(String.format(
        				"Error: Failed to unset exclusion on reference: %s\n", 
        				m_reference));
    	    }
        	break;
        }    	
    }

    private void listExclusions(Engine engine, Console console) {
    	Collection<Reference> exclusions = null;
    	
        switch (m_mode) {
        case GLOBAL:
    	    exclusions = engine.getGlobalExclusions();
        	break;
        	
        case ROUTE:
    	    exclusions = engine.getRouteExclusions(m_routeId);
        	break;
        }

        if (exclusions == null)
	    	return;
        	    
	    if (exclusions.size() > 0) {
	    	for (Reference exclusion : exclusions) {
	    	    console.display(String.format("- 0x%x: %s", 
	    	    		exclusion.getPc(), exclusion.getRef()));
	    	}    	    	
	    } else {
	    	console.display("none\n");
	    }
    }
 }
