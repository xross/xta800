package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Time extends Command {
	private enum Mode {
		NS,
		CYCLES,
	}
	
	private int m_pc;
	private Mode m_mode;
	private int m_numCycles;
	private float m_timeTaken;
	
	Time() {
		m_pc = 0;
		m_mode = Mode.NS;
		m_numCycles = 0;
		m_timeTaken = 0;
	}
	
	Time(int pc, int cycles) {
		m_mode = Mode.CYCLES;
		m_pc = pc;
		m_numCycles = cycles;
		m_timeTaken = 0;
	}

	Time(int pc, float time) {
		m_mode = Mode.NS;
		m_pc = pc;
		m_timeTaken = time;
		m_numCycles = 0;
	}
	
    public int numArgs() {
    	return 3;
    }        

    public String name() {
    	return "time";
    }        

    public String commandLine() {
    	switch (m_mode) {
    	case NS:
        	return String.format("time ns 0x%05x %.1f", m_pc, m_timeTaken);
    	case CYCLES:
        	return String.format("time cycles 0x%05x %d", m_pc, m_numCycles);    		
    	}
    	assert(false);
    	return "";
    }
    
    public String description(Engine engine, boolean detailed) {
        String descString = "time <pc> <mode> <value>: Sets the time taken for the instruction at the given pc";         
    	if (detailed) {    	    	
        	descString += "\n";
    		descString += "          mode: ns:     Sets the time this instruction takes in ns\n";
    		descString += "          mode: cycles: Sets the time this instruction takes in thread cycles";
    	}
    	return descString;
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int pc;
    	try {
    		// Strips the 0x off the beginning of the string if present.
            String hexString = args[index + 1];
            if (hexString.matches("0x[0-9a-fA-F]*"))
                hexString = hexString.substring(2);  
            
    	    pc = Integer.parseInt(hexString, 16);
	    }
	    catch (NumberFormatException e) {
  	    	return null;
	    }    
		
		if (args[index + 2].equals("ns")) {			
		    float ns;
	        try { ns = Float.parseFloat(args[index + 3]); }
	        catch (NumberFormatException e) {
	            return null;
	        }    
	        return new Time(pc, ns);
		    
		}
		else if (args[index + 2].equals("cycles")) {
		    int cycles;
	        try { cycles = Integer.parseInt(args[index + 3]); }
	        catch (NumberFormatException e) {
	            return null;
	        }    
	        return new Time(pc, cycles);
		}
		else {
			return null;
		}
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	switch (m_mode) {
    	case NS:
            if (!engine.setInstructionTimeTaken(m_pc, m_timeTaken)) {  
             	console.display(String.format("Error: %s\n", engine.getErrorString()));
            }            
    		break;
    	case CYCLES:
            if (!engine.setInstructionNumCycles(m_pc, m_numCycles)) {  
             	console.display(String.format("Error: %s\n", engine.getErrorString()));
            }            
    		break;
        default:
        	assert(false);
            break;        	
    	}    	    	
    }    
}
