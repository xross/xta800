package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class Asm extends Command {
	private int m_id;
	private Command.IdType m_idType;

	public Asm() {
		m_id = 0;
		m_idType = null;
	}

	public Asm(int id, IdType idType) {
		m_id = id;
		m_idType = idType;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "asm";
    }        

    public String commandLine() {
    	return "asm " + m_id;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "asm <path/block id>: Display annotated assembly for given path id";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		
    	
    	try {
    	    return new Asm(getId(args[index + 1]), getIdType(args[index + 1]));
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
		
		AsmDisplayer displayer = new AsmDisplayer(engine, console, m_id, m_idType); 
        displayer.display();
   } 
}
