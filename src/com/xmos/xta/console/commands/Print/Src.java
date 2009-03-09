package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Src extends Command {
	private int m_routeId;
	private int m_id;
	private Command.IdType m_idType;

	public Src() {
		m_routeId = 0;
		m_id = 0;
		m_idType = null;
	}

	public Src(int routeId, int id, IdType idType) {
		m_routeId = routeId;
		m_id = id;
		m_idType = idType;
	}
	
    public int numArgs() {
    	return 2;
    }        

    public String name() {
    	return "src";
    }        

    public String commandLine() {
    	return "src " + m_routeId + " " + m_id;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "src <route id> <path/block id>: Display annotated source file(s) for given path id";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	try {
    		return new Src(getRouteId(args[index + 1]), 
    				getId(args[index + 2]), getIdType(args[index + 2]));
    	}
    	catch (NumberFormatException e) {
    	    return null;
    	}
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateIds(engine, console, m_routeId, m_id, m_idType))
		    return;
				
		SrcDisplayer displayer = new SrcDisplayer(engine, console, m_routeId, m_id, m_idType, false);
		displayer.display();	
   }    
}
