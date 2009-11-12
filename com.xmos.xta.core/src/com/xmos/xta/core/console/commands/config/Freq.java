package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Freq extends Command {
	private String m_frequencyString;
	
	public Freq() {
		m_frequencyString = "";
	}
	
	public Freq(String frequencyString) {
		m_frequencyString = frequencyString;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "freq";
    }        

    public String commandLine() {
    	return String.format("freq %s", m_frequencyString);
    }
    
    public String description(Engine engine, boolean detailed) {
        return String.format("freq <core frequency>: Sets the core frequency in Mhz (currently: %f)", 
        		engine.getFrequency());
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

        try { Double.parseDouble(args[index + 1]); }
        catch (NumberFormatException e) {
            return null;
        }    	        
        return new Freq(args[index + 1]);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        engine.setFrequency(getSrc(), Double.parseDouble(m_frequencyString));
    }    
}
