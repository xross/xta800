package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Freq extends Command {
	private int m_frequency;
	
	Freq() {
		m_frequency = 0;
	}
	
	Freq(int frequency) {
		m_frequency = frequency;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "freq";
    }        

    public String commandLine() {
    	return String.format("freq %d", m_frequency);
    }
    
    public String description(Engine engine, boolean detailed) {
        return String.format("freq <core frequency>: Sets the core frequency (default: %d)", 
        		engine.getFrequency());      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int frequency;
        try { frequency = Integer.parseInt(args[index + 1]); }
        catch (NumberFormatException e) {
            return null;
        }    	        
        return new Freq(frequency);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        engine.setFrequency(m_frequency);
    }    
}
