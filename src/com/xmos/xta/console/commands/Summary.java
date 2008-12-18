package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.SummaryDisplayer;
import com.xmos.xta.engine.Engine;

class Summary extends Command {
	private SummaryDisplayer.Level m_level;
	private int m_pathId;
	
	Summary() {
		m_level = SummaryDisplayer.Level.error;
		m_pathId = 0;
	}
	
	Summary(SummaryDisplayer.Level level) {
		m_level = level;
		m_pathId = 0;
	}

	Summary(int pathId) {
		m_level = SummaryDisplayer.Level.pathid;
		m_pathId = pathId;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "summary";
    }        

    public String commandLine() {
    	if (m_level == SummaryDisplayer.Level.pathid)
    	    return "summary " + m_pathId;
    	else
    	    return "summary " + m_level;    	
    }        
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("summary <path id | level>: Display timing summary for paths/functions");
    	if (detailed) {    	    	
        	descString += "\n";
    		descString += SummaryDisplayer.getLevelOptionString();
    	}
    	return descString;
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int pathId = 0;
        try { 
        	pathId = Integer.parseInt(args[index + 1]);         
        	return new Summary(pathId);
        }
        catch (NumberFormatException e) {        	
        	SummaryDisplayer.Level level = SummaryDisplayer.checkLevel(args[index + 1]);	        	
        	if (level == SummaryDisplayer.Level.error)
    	    	return null;
        	
        	return new Summary(level);        	
        }    		    
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;
		
        engine.displayRoute(new SummaryDisplayer(console, m_level, m_pathId, engine.getXeObject()));	
    }
}
