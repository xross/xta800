package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.TraceDisplayer;
import com.xmos.xta.engine.Engine;

class Trace extends Command {
	private int m_pathId;

	Trace() {
		m_pathId = 0;
	}
	
	Trace(int pathId) {
		m_pathId = pathId;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "trace";
    }        

    public String commandLine() {
    	return "trace " + m_pathId;
    }        
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("trace <path id>: Display instruction trace for the given path id");
    	return descString;    	                
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int pathId;
    	try {
    	    pathId = Integer.parseInt(args[index + 1]);
    	    return new Trace(pathId);
	    }
	    catch (NumberFormatException e) {
   	    	return null;
	    }    	
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        engine.displayRoute(new TraceDisplayer(console, m_pathId));	
    }    
}
