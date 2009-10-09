package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Required extends Command {
	private enum Mode {
		NS,
		MHZ,
		CYCLES,
	}

	private int m_routeId;
	private Mode m_mode;
	private long m_cycles;
	private double m_ns;
	private double m_mhz;
	
	public Required() {
		m_mode = Mode.NS;
		m_ns = 0;
		m_mhz = 0;
		m_cycles = 0;
	}
	
	public Required(int routeId, long cycles) {
		m_mode = Mode.CYCLES;
		m_routeId = routeId;
		m_ns = 0;
		m_mhz = 0;
		m_cycles = cycles;
	}

	public Required(int routeId, double value, Mode mode) {
		assert((mode == Mode.NS) || (mode == Mode.MHZ));
		m_mode = mode;
		m_routeId = routeId;
		m_cycles = 0;

		switch (mode) {
		case NS: 
		    m_ns = value;
		    break;
		case MHZ:
		    m_mhz = value;
		    break;
		}			
	}

    public int numArgs() {
   		return 3; 
    }

    public String name() {
    	return "required";
    }        

    public String commandLine() {
       	switch (m_mode) {
       	case NS:
           	return String.format("required %s %.1f ns", getRouteId(m_routeId), m_ns);
       	case MHZ:
           	return String.format("required %s %.1f mhz", getRouteId(m_routeId), m_mhz);
       	case CYCLES:
           	return String.format("required %s %d cycles", getRouteId(m_routeId), m_cycles);    		
       	}
    	assert(false);
    	return "";
    }
    
    public String description(Engine engine, boolean detailed) {
        String descString = "required <route id> <value> <mode>: Sets the maximum allowed time taken for the given route";         
      	if (detailed) {    	    	
           	descString += "\n";
       		descString += "          mode: ns:     Sets the required route time in ns\n";
       		descString += "          mode: mhz:    Sets the required route time in mhz\n";
       		descString += "          mode: cycles: Sets the required route time in thread cycles";
       	}
       	return descString;
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length) 
			return null;
    	int routeId;
    	try {routeId  = getRouteId(args[index + 1]);    	}
    	catch (NumberFormatException e) {
	        return null;
	    }
   		String value = args[index + 2];
    	String mode = args[index + 3];

    	if (mode.equals("ns")) {			
    	    double ns;
    	    try { ns = Double.parseDouble(value); }
    	    catch (NumberFormatException e) {
    	        return null;
    	    }
    	    return new Required(routeId, ns, Mode.NS);
    	        
    	} else if (mode.equals("mhz")) {
    		double mhz;
    	    try { mhz = Double.parseDouble(value); }
    	    catch (NumberFormatException e) {
    	        return null;
    	    }
    	    return new Required(routeId, mhz, Mode.MHZ);
    		    
    	} else if (mode.equals("cycles")) {
    	    long cycles;
    	    try { cycles = Long.parseLong(value); }
    	    catch (NumberFormatException e) {
    	        return null;
    	    }
    	    return new Required(routeId, cycles);    	        
    	        
    	} else {
    		return null;
    	}
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;    	
    	
   		switch (m_mode) {
    	case NS:
            engine.setRequiredTime(getSrc(), m_routeId, m_ns);
    		return;
    	case MHZ:
            engine.setRequiredMhz(getSrc(), m_routeId, m_mhz);
    		return;
    	case CYCLES:
            engine.setRequiredCycles(getSrc(), m_routeId, m_cycles);
    		return;
    	}
    	assert(false);
    }    
}
