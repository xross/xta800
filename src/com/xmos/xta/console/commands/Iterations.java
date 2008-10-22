package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Iterations extends Command {
	int m_iterations;
	boolean m_absolute;
	
	Iterations() {
		m_iterations = 0;
		m_absolute = false;
	}
	
	Iterations(boolean absolute, int iterations) {
		m_iterations = iterations;
		m_absolute = absolute;
	}
	
    public int numArgs() {
    	return 2;
    }        

    public String name() {
    	return "iterations";
    }        

    public String commandLine() {
    	String absolute = m_absolute ? "a" : "r";
    	return String.format("iterations %s %d", absolute, m_iterations);
    }
    
    public String description(Engine engine, boolean detailed) {
        return String.format("iterations <a|r> <num>: Sets the default number of loop iterations (a: absolute, r: relative)");      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

	    boolean absolute;
	    if (args[index + 1].compareTo("r") == 0){
	    	absolute = false;
	    }
	    else if (args[index + 1].compareTo("a") == 0){
	    	absolute = true;
	    }
	    else {
	    	return null;
	    }
	    	    	    
    	int iterations;
    	try { iterations = Integer.parseInt(args[index + 2]); }
	    catch (NumberFormatException e) {
	        return null;
	    }    		  
        return new Iterations(absolute, iterations);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
//        engine.getConfig().m_defaultLoopIterations = m_iterations;
//        engine.getConfig().m_defaultLoopAbsolute = m_absolute;
    }    
}
