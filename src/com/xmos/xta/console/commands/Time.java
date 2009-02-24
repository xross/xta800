package com.xmos.xta.console.commands;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;

public class Time extends Command {
	public enum Operation {
		SET,
		UNSET,
	}

	private enum Mode {
		NS,
		CYCLES,
	}
	
	private String m_refString;
	private Mode m_mode;
	private Operation m_operation;
	private int m_numCycles;
	private float m_timeTaken;
	
	public Time(Operation operation) {
		m_operation = operation;
		m_mode = Mode.NS;
		m_numCycles = 0;
		m_timeTaken = 0;
	}
	
	public Time(String refString, int cycles) {
		m_operation = Operation.SET;
		m_mode = Mode.CYCLES;
		m_refString = refString;
		m_numCycles = cycles;
		m_timeTaken = 0;
	}

	public Time(String refString, float time) {
		m_operation = Operation.SET;
		m_mode = Mode.NS;
		m_refString = refString;
		m_timeTaken = time;
		m_numCycles = 0;
	}

	public Time(String refString) {
		m_operation = Operation.UNSET;
		m_mode = Mode.NS;
		m_refString = refString;
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
    	return "time";
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case SET:
        	switch (m_mode) {
        	case NS:
            	return String.format("time %s ns %.1f", m_refString, m_timeTaken);
        	case CYCLES:
            	return String.format("time %s cycles %d", m_refString, m_numCycles);    		
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
            String descString = "time <ref> <mode> <value>: Sets the time taken for the instruction at the given pc";         
        	if (detailed) {    	    	
            	descString += "\n";
        		descString += "          mode: ns:     Sets the time this instruction takes in ns\n";
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
    		if (args[index + 2].equals("ns")) {			
    		    float ns;
    	        try { ns = Float.parseFloat(args[index + 3]); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }    
    	        return new Time(args[index + 1], ns);
    		    
    		} else if (args[index + 2].equals("cycles")) {
    		    int cycles;
    	        try { cycles = Integer.parseInt(args[index + 3]); }
    	        catch (NumberFormatException e) {
    	            return null;
    	        }    
    	        return new Time(args[index + 1], cycles);
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
		CodeReference ref = CodeReferenceFactory.createEndpointReference(m_refString);

		switch (m_operation) {
    	case SET:
    		switch (m_mode) {
	    	case NS:
	            engine.setInstructionTimeTaken(ref, m_timeTaken);
	    		return;
	    	case CYCLES:
	            engine.setInstructionNumCycles(ref, m_numCycles);
	    		return;
	    	}
    	case UNSET:
    		engine.unsetInstructionTime(ref);
    		return;
    	}
    	assert(false);
    }    
}
