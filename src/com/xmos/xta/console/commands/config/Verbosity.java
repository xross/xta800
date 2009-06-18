package com.xmos.xta.console.commands.config;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Verbosity extends Command {
	private int m_verbosity;
	
	public Verbosity() {
		m_verbosity = 0;
	}
	
	public Verbosity(int verbosity) {
		m_verbosity = verbosity;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "verbosity";
    }        

    public String commandLine() {
    	return String.format("verbosity %d", m_verbosity);
    }
    
    public String description(Engine engine, boolean detailed) {
        return "verbosity <level>: Sets the tool verbosity level (range: -10 -> +10, default: 0)"; 
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int verbosity;
        try { verbosity = Integer.parseInt(args[index + 1]); }
        catch (NumberFormatException e) {
            return null;
        }    	        
        return new Verbosity(verbosity);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        engine.setVerbosity(m_verbosity);
    }    
}
