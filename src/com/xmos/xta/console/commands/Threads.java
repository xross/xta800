package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Threads extends Command {
	private int m_numThreads;
	
	Threads() {
		m_numThreads = 0;
	}
	
	Threads(int threads) {
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
        return String.format("threads <num threads>: Sets number of threads currently executing (default: %d)", 
        		engine.getNumThreads());      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
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
        engine.setNumThreads(m_numThreads);
    }    
}
