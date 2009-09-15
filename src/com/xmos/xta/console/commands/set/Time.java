package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class Time extends Command {
	private enum Mode {
		NS,
		MHZ,
		CYCLES,
	}

	private int m_routeId;
	private String m_refString;
	private Mode m_mode;
	private float m_ns;
	private float m_mhz;
	private int m_cycles;
	
	public Time() {
		m_routeId = 0;
		m_mode = Mode.NS;
		m_ns = 0;
		m_mhz = 0;
		m_cycles = 0;
	}
	
	public Time(int routeId, String refString, int cycles) {
		m_routeId = routeId;
		m_mode = Mode.CYCLES;
		m_refString = refString;
		m_cycles = cycles;
		m_ns = 0;
		m_mhz = 0;
	}

	public Time(int routeId, String refString, float value, Mode mode) {
		m_routeId = routeId;
		m_mode = Mode.NS;
		m_refString = refString;
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

    public int numArgs() {
   		return 4; 
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.ENDPOINT};
	}    
    
    public String name() {
    	return "time";
    }        

    public String commandLine() {
       	switch (m_mode) {
       	case NS:
           	return String.format("time %s %s %.1f ns", getRouteId(m_routeId), m_refString, m_ns);
       	case MHZ:
           	return String.format("time %s %s %.1f mhz", getRouteId(m_routeId), m_refString, m_mhz);
       	case CYCLES:
           	return String.format("time %s %s %d cycles", getRouteId(m_routeId), m_refString, m_cycles);    		
       	}
       	assert(false);
       	return "";
    }
    
    public String description(Engine engine, boolean detailed) {
        String descString = "time <route id> <ENDPOINT_REF> <value> <mode>: Sets the time taken for the instruction at the given pc";         
       	if (detailed) {    	    	
           	descString += "\n";
       		descString += "          mode: ns:     Sets the time this instruction takes in ns\n";
       		descString += "          mode: mhz:    Sets the time this instruction takes in mhz\n";
       		descString += "          mode: cycles: Sets the time this instruction takes in thread cycles";
       	}
       	return descString;
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length) 
			return null;

		int routeId;
    	try { routeId = getRouteId(args[index + 1]);
	    } catch (NumberFormatException e) {
	        return null;
	    }    			
    	
   		String ref = args[index + 2];
   		String value = args[index + 3];
   		String mode = args[index + 4];
    		
   		if (mode.equals("ns")) {			
   		    float ns;
   	        try { ns = Float.parseFloat(value); }
   	        catch (NumberFormatException e) {
   	            return null;
   	        }    
   	        return new Time(routeId, ref, ns, Mode.NS);
   		    
   		} else if (mode.equals("mhz")) {
   		    float mhz;
   	        try { mhz = Float.parseFloat(value); }
   	        catch (NumberFormatException e) {
   	            return null;
   	        }    
   	        return new Time(routeId, ref, mhz, Mode.MHZ);
    		    
   		} else if (mode.equals("cycles")) {
   		    int cycles;
   	        try { cycles = Integer.parseInt(value); }
   	        catch (NumberFormatException e) {
   	            return null;
   	        }    
   	        return new Time(routeId, ref, cycles);
    	        
   		} else {
   			return null;
   		}
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	    	
    	
		Reference ref = ReferenceFactory.createReference(m_refString, ReferenceClass.ENDPOINT_REF);
		ref.resolve(engine, engine.getNodeId(), engine.getCoreId());

   		switch (m_mode) {
	    case NS:
	        if (!engine.setInstructionTimeTaken(getSrc(), m_routeId, m_refString, m_ns))
	   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
	       			"failed to set instruction time on reference id: %s", m_refString));    			            	
	    	break;

	    case MHZ:
	        if (!engine.setInstructionTimeTaken(getSrc(), m_routeId, m_refString, m_mhz))
	   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
        		    "failed to set instruction time on reference id: %s", m_refString));    			            	
	        break;
	    		
	    case CYCLES:
	        if (!engine.setInstructionNumCycles(getSrc(), m_routeId, m_refString, m_cycles))
	   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
        			"failed to set instruction time on reference id: %s", m_refString));    			            	
	        break;
	    }
    }    
}
