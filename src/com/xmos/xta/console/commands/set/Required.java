package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Required extends Command {
	public enum Operation {
		SET,
		UNSET,
	}

	private enum Mode {
		NS,
		MHZ,
		CYCLES,
	}
	
	private int m_routeId;
	private Mode m_mode;
	private Operation m_operation;
	private int m_cycles;
	private float m_ns;
	private float m_mhz;
	
	public Required(Operation operation) {
		m_operation = operation;
		m_mode = Mode.NS;
		m_ns = 0;
		m_mhz = 0;
		m_cycles = 0;
	}
	
	public Required(int routeId, int cycles) {
		m_operation = Operation.SET;
		m_mode = Mode.CYCLES;
		m_routeId = routeId;
		m_ns = 0;
		m_mhz = 0;
		m_cycles = cycles;
	}

	public Required(int routeId, float value, Mode mode) {
		assert((mode == Mode.NS) || (mode == Mode.MHZ));
		m_operation = Operation.SET;
		m_mode = mode;
		m_routeId = routeId;
		m_cycles = 0;

		switch (mode) {
		case NS: 
		    m_ns = value;
		    m_mhz = ((float)1.0 / value) * 1000;
		    break;
		case MHZ:
		    m_ns = ((float)1.0 / value) * 1000;
		    m_mhz = value;
		    break;
		}			
	}

	public Required(int routeId) {
		m_operation = Operation.UNSET;
		m_mode = Mode.NS;
		m_routeId = routeId;
		m_cycles = 0;
		m_ns = 0;
		m_mhz = 0;
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
    	return "required";
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case SET:
        	switch (m_mode) {
        	case NS:
            	return String.format("required %s %.1f ns", getRouteId(m_routeId), m_ns);
        	case MHZ:
            	return String.format("required %s %.1f mhz", getRouteId(m_routeId), m_mhz);
        	case CYCLES:
            	return String.format("required %s %d cycles", getRouteId(m_routeId), m_cycles);    		
        	}
        	break;
        	
    	case UNSET:
    		return String.format("required %s", getRouteId(m_routeId));
    	}    			

    	assert(false);
    	return "";
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case SET:
            String descString = "required <route id> <value> <mode>: Sets the maximum allowed time taken for the given route";         
        	if (detailed) {    	    	
            	descString += "\n";
        		descString += "          mode: ns:     Sets the required route time in ns\n";
        		descString += "          mode: mhz:    Sets the required route time in mhz\n";
        		descString += "          mode: cycles: Sets the required route time in thread cycles";
        	}
        	return descString;
    	case UNSET:
            return "required <route id>: Clears the the maximum allowed time for the given route";
    	}

    	assert(false);
    	return "";
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length) 
			return null;
    	
    	int routeId = getRouteId(args[index + 1]);
    	
    	switch (m_operation) {
    	case SET:
    		String value = args[index + 2];
    		String mode = args[index + 3];

    		if (mode.equals("ns")) {			
    		    float ns;
    	        try { ns = Float.parseFloat(value); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }
    	        return new Required(routeId, ns, Mode.NS);
    	        
    		} else if (mode.equals("mhz")) {
    		    float mhz;
    	        try { mhz = Float.parseFloat(value); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }
    	        return new Required(routeId, mhz, Mode.MHZ);
    		    
    		} else if (mode.equals("cycles")) {
    		    int cycles;
    	        try { cycles = Integer.parseInt(value); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }
    	        return new Required(routeId, cycles);    	        
    	        
    		} else {
    			return null;
    		}
    	case UNSET:
            return new Required(routeId);
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
	    	case MHZ:
	            engine.setRouteMaxTime(m_routeId, m_ns);
	    		return;
	    	case CYCLES:
	            engine.setRouteMaxCycles(m_routeId, m_cycles);
	    		return;
	    	}
    	case UNSET:
    		engine.unsetRouteMax(m_routeId);
    		return;
    	}
    	assert(false);
    }    
}
