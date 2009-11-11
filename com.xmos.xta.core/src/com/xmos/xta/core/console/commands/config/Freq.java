package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Freq extends Command {
	private int m_frequency;
	
	public Freq() {
		m_frequency = 0;
	}
	
	public Freq(int frequency) {
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
        return String.format("freq <core frequency>: Sets the core frequency in Mhz (currently: %d)", 
        		(engine.getFrequencyHz() / 1000000));      	
    }

    public Command clone(String[] args, int index) {
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
        engine.setFrequencyHz(getSrc(), m_frequency * 1000000);
    }    
}
