package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class Time extends Command {
	public enum Operation {
		SET,
		UNSET,
	}

	private enum Mode {
		NS,
		MHZ,
		CYCLES,
	}
	
	private String m_refString;
	private Mode m_mode;
	private Operation m_operation;
	private float m_ns;
	private float m_mhz;
	private int m_cycles;
	
	public Time(Operation operation) {
		m_operation = operation;
		m_mode = Mode.NS;
		m_ns = 0;
		m_mhz = 0;
		m_cycles = 0;
	}
	
	public Time(String refString, int cycles) {
		m_operation = Operation.SET;
		m_mode = Mode.CYCLES;
		m_refString = refString;
		m_cycles = cycles;
		m_ns = 0;
		m_mhz = 0;
	}

	public Time(String refString, float value, Mode mode) {
		m_operation = Operation.SET;
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

	public Time(String refString) {
		m_operation = Operation.UNSET;
		m_mode = Mode.NS;
		m_refString = refString;
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
    	return "time";
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case SET:
        	switch (m_mode) {
        	case NS:
            	return String.format("time %s %.1f ns", m_refString, m_ns);
        	case MHZ:
            	return String.format("time %s %.1f mhz", m_refString, m_mhz);
        	case CYCLES:
            	return String.format("time %s %d cycles", m_refString, m_cycles);    		
        	}
    	case UNSET:
    		return String.format("time %s", m_refString);
    	}    			

    	assert(false);
    	return "";
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case SET:
            String descString = "time <ENDPOINT_REF> <value> <mode>: Sets the time taken for the instruction at the given pc";         
        	if (detailed) {    	    	
            	descString += "\n";
        		descString += "          mode: ns:     Sets the time this instruction takes in ns\n";
        		descString += "          mode: mhz:    Sets the time this instruction takes in mhz\n";
        		descString += "          mode: cycles: Sets the time this instruction takes in thread cycles";
        	}
        	return descString;
    	case UNSET:
            return "time <pc>: Clears the time set for the given instructions (supports regular expressions)";
    	}

    	assert(false);
    	return "";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	if ((index + numArgs()) >= args.length) 
			return null;
    	
    	switch (m_operation) {
    	case SET:
    		String ref = args[index + 1];
    		String value = args[index + 2];
    		String mode = args[index + 3];
    		
    		if (mode.equals("ns")) {			
    		    float ns;
    	        try { ns = Float.parseFloat(value); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }    
    	        return new Time(ref, ns, Mode.NS);
    		    
    		} else if (mode.equals("mhz")) {
    		    float mhz;
    	        try { mhz = Float.parseFloat(value); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }    
    	        return new Time(ref, mhz, Mode.MHZ);
    		    
    		} else if (mode.equals("cycles")) {
    		    int cycles;
    	        try { cycles = Integer.parseInt(value); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }    
    	        return new Time(ref, cycles);
    	        
    		} else {
    			return null;
    		}
    	case UNSET:
            return new Time(args[index + 1]);
    	}
    	assert(false);
    	return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		Reference ref = ReferenceFactory.createReference(m_refString, ReferenceClass.ENDPOINT_REF);
		ref.resolve(engine, engine.getNodeId(), engine.getCoreId());

		switch (m_operation) {
    	case SET:
    		switch (m_mode) {
	    	case NS:
	            engine.setInstructionTimeTaken(ref, m_ns);
	    		return;
	    	case MHZ:
	            engine.setInstructionTimeTaken(ref, m_mhz);
	    		return;
	    	case CYCLES:
	            engine.setInstructionNumCycles(ref, m_cycles);
	    		return;
	    	}
    	case UNSET:
    		for (Route route : engine.getRoutes()) {
    			engine.unsetInstructionTime(ref, route.getUnknowns().getUnknownInstructionTimes());
    		}
    		return;
    	}
    	assert(false);
    }    
}
