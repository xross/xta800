package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.TraceDisplayer;
import com.xmos.xta.engine.Engine;

class Trace extends Command {
	TraceDisplayer.Level m_level;
	int m_pathId;

	Trace() {
		m_level = TraceDisplayer.Level.ERROR;
		m_pathId = 0;
	}
	
	Trace(TraceDisplayer.Level level) {
		m_level = level;
		m_pathId = 0;
	}	

	Trace(int pathId) {
		m_level = TraceDisplayer.Level.PATHID;
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
    	String descString = String.format("trace <path id | level>: Display instruction trace for the given path id");
    	if (detailed) {    	    	
        	descString += "\n";
    		descString += TraceDisplayer.getLevelOptionString();
    	}
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
	    	TraceDisplayer.Level level = TraceDisplayer.checkLevel(args[index + 1]);	        	
        	if (level == TraceDisplayer.Level.ERROR)
    	    	return null;
        	
        	return new Trace(level);        	
	    }    	
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        engine.displayRoute(new TraceDisplayer(console, m_level, m_pathId));	
    }    
}
