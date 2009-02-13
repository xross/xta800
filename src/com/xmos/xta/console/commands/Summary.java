package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.SummaryDisplayer;
import com.xmos.xta.engine.Engine;

class Summary extends Command {
	private SummaryDisplayer.Level m_level;
	private int m_id;
	private Command.IdType m_idType;
	
	Summary() {
		m_level = SummaryDisplayer.Level.error;
		m_id = 0;
		m_idType = null;
	}
	
	Summary(SummaryDisplayer.Level level) {
		m_level = level;
		m_id = 0;
		m_idType = null;
	}

	Summary(SummaryDisplayer.Level level, int id, IdType idType) {
		m_level = level;
		m_id = id;
		m_idType = idType;
	}
	
    public int numArgs() {
    	if (m_level == SummaryDisplayer.Level.info)
        	return 2;
    	else 
    		return 1;
    }        

    public String name() {
    	return "summary";
    }        

    public String commandLine() {
    	if (m_level == SummaryDisplayer.Level.info)
    	    return "summary " + m_level + m_id;
    	else
    	    return "summary " + m_level;    	
    }        
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("summary <level> [path/block id]: Display timing summary for paths/functions");
    	if (detailed) {    	    	
        	descString += "\n";
    		descString += SummaryDisplayer.getLevelOptionString();
    	}
    	return descString;
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	SummaryDisplayer.Level level = SummaryDisplayer.checkLevel(args[index + 1]);	        	
    	if (level == SummaryDisplayer.Level.error)
	    	return null;
		
    	if (areArgsDone(args, index + 2)) 
    		return new Summary(level);
    		
        try { 
            return new Summary(level, getId(args[index + 2]), getIdType(args[index + 2]));
        } catch (NumberFormatException e) {        	
          	return null;
        }    		            		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;
		
		SummaryDisplayer displayer = new SummaryDisplayer(engine, console, m_level, m_id, m_idType);
		displayer.display();
    }
}
