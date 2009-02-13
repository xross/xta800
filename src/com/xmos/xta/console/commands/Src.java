package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.SrcDisplayer;
import com.xmos.xta.engine.Engine;

class Src extends Command {
	private int m_id;
	private Command.IdType m_idType;

	Src() {
		m_id = 0;
		m_idType = null;
	}

	Src(int id, IdType idType) {
		m_id = id;
		m_idType = idType;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "src";
    }        

    public String commandLine() {
    	return "src " + m_id;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "src <path/block id>: Display annotated source file(s) for given path id";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	try {
    		return new Src(getId(args[index + 1]), getIdType(args[index + 1]));
    	}
    	catch (NumberFormatException e) {
    	    return null;
    	}
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;

	    if (!isIdValid(engine, console, m_id, m_idType))
		    return;
				
		SrcDisplayer displayer = new SrcDisplayer(engine, console, m_id, m_idType, false);
		displayer.display();	
   }    
}
