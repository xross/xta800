package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class MaxTime extends Command {
	public enum Operation {
		SET,
		UNSET,
	}

	private enum Mode {
		NS,
		CYCLES,
	}
	
	private int m_routeId;
	private Mode m_mode;
	private Operation m_operation;
	private int m_numCycles;
	private float m_timeTaken;
	
	public MaxTime(Operation operation) {
		m_operation = operation;
		m_mode = Mode.NS;
		m_numCycles = 0;
		m_timeTaken = 0;
	}
	
	public MaxTime(int routeId, int cycles) {
		m_operation = Operation.SET;
		m_mode = Mode.CYCLES;
		m_routeId = routeId;
		m_numCycles = cycles;
		m_timeTaken = 0;
	}

	public MaxTime(int routeId, float time) {
		m_operation = Operation.SET;
		m_mode = Mode.NS;
		m_routeId = routeId;
		m_timeTaken = time;
		m_numCycles = 0;
	}

	public MaxTime(int routeId) {
		m_operation = Operation.UNSET;
		m_mode = Mode.NS;
		m_routeId = routeId;
		m_timeTaken = 0;
		m_numCycles = 0;
	}

    public int numArgs() {
    	switch (m_operation) {
    	case SET:
    		return 3; 
    	case UNSET:
    		return 1;
    	}    			
    	assert(false);
        return 0;
    }

    public String name() {
    	return "maxtime";
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case SET:
        	switch (m_mode) {
        	case NS:
            	return String.format("maxtime %s ns %.1f", getRouteId(m_routeId), m_timeTaken);
        	case CYCLES:
            	return String.format("maxtime %s cycles %d", getRouteId(m_routeId), m_numCycles);    		
        	}
        	break;
        	
    	case UNSET:
    		return String.format("maxtime %s", getRouteId(m_routeId));
    	}    			

    	assert(false);
    	return "";
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case SET:
            String descString = "maxtime <route id> <mode> <value>: Sets the maximum allowed time taken for the given route";         
        	if (detailed) {    	    	
            	descString += "\n";
        		descString += "          mode: ns:     Sets the time this instruction takes in ns\n";
        		descString += "          mode: cycles: Sets the time this instruction takes in thread cycles";
        	}
        	return descString;
    	case UNSET:
            return "time <route id>: Clears the the maximum allowed time for the given route";
    	}

    	assert(false);
    	return "";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	if ((index + numArgs()) >= args.length) 
			return null;
    	
    	int routeId = getRouteId(args[index + 1]);
    	
    	switch (m_operation) {
    	case SET:
    		if (args[index + 2].equals("ns")) {			
    		    float ns;
    	        try { ns = Float.parseFloat(args[index + 3]); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }
    	        return new MaxTime(routeId, ns);
    		    
    		} else if (args[index + 2].equals("cycles")) {
    		    int cycles;
    	        try { cycles = Integer.parseInt(args[index + 3]); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }    
    	        return new MaxTime(routeId, cycles);
    		} else {
    			return null;
    		}
    	case UNSET:
            return new MaxTime(routeId);
    	}
    	assert(false);
    	return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;    	
    	
		switch (m_operation) {
    	case SET:
    		switch (m_mode) {
	    	case NS:
	            engine.setRouteMaxTime(m_routeId, m_timeTaken);
	    		return;
	    	case CYCLES:
	            engine.setRouteMaxCycles(m_routeId, m_numCycles);
	    		return;
	    	}
    	case UNSET:
    		engine.unsetRouteMax(m_routeId);
    		return;
    	}
    	assert(false);
    }    
}
