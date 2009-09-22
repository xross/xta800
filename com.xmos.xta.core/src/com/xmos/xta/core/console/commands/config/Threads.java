package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Threads extends Command {
	private int m_numThreads;
	
	public Threads() {
		m_numThreads = 0;
	}
	
	public Threads(int threads) {
		m_numThreads = threads;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "threads";
    }        

    public String commandLine() {
    	return String.format("threads %d", m_numThreads);
    }
    
    public String description(Engine engine, boolean detailed) {
        return String.format("threads <num threads>: Sets number of threads currently executing (currently: %d)", 
        		engine.getNumThreads());      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int numThreads;
        try { numThreads = Integer.parseInt(args[index + 1]); }
        catch (NumberFormatException e) {
            return null;
        }    	        
        return new Threads(numThreads);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        engine.setNumThreads(getSrc(), m_numThreads);
    }    
}
