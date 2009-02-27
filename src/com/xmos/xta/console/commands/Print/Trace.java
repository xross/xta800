package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Trace extends Command {
	private int m_id;
	private Command.IdType m_idType;

	public Trace() {
		m_id = 0;
		m_idType = null;
	}
	
	public Trace(int id, Command.IdType idType) {
		m_id = id;		
		m_idType = idType;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "trace";
    }        

    public String commandLine() {
    	return "trace " + m_id;
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("trace <path/block id>: Display instruction trace for the given path/block");
    	return descString;    	                
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	try {
    	    return new Trace(getId(args[index + 1]), getIdType(args[index + 1]));
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
		
		TraceDisplayer displayer = new TraceDisplayer(engine, console, m_id, m_idType);	
		displayer.display();
    }    
}
