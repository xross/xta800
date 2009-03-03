package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Mix extends Command {
	private int m_id;
	private Command.IdType m_idType;

	public Mix() {
		m_id = 0;
		m_idType = null;
	}

	public Mix(int id, IdType idType) {
		m_id = id;
		m_idType = idType;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "mix";
    }        

    public String commandLine() {
    	return "mix " + m_id;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "mix <path/block id>: Display annotated source file(s) + assembly instructions for the given path id";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	try {
    		return new Mix(getId(args[index + 1]), getIdType(args[index + 1]));
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
		
		SrcDisplayer displayer = new SrcDisplayer(engine, console, m_id, m_idType, true);
		displayer.display();
   }    
}
