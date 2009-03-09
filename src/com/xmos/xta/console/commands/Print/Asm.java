package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Asm extends Command {
	private int m_routeId;
	private int m_id;
	private Command.IdType m_idType;

	public Asm() {
		m_routeId = 0;
		m_id = 0;
		m_idType = null;
	}

	public Asm(int routeId, int id, IdType idType) {
		m_routeId = routeId;
		m_id = id;
		m_idType = idType;
	}
	
    public int numArgs() {
    	return 2;
    }        

    public String name() {
    	return "asm";
    }        

    public String commandLine() {
    	return "asm " + m_routeId + " " + m_id;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "asm <route id> <path/block id>: Display annotated assembly for given path id";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		
    	
    	try {
    	    return new Asm(getRouteId(args[index + 1]), 
    	    		getId(args[index + 2]), getIdType(args[index + 2]));
	    } catch (NumberFormatException e) {
	        return null;
	    }    	
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateIds(engine, console, m_routeId, m_id, m_idType))
		    return;
		
		AsmDisplayer displayer = new AsmDisplayer(engine, console, m_routeId, m_id, m_idType); 
        displayer.display();
   } 
}
