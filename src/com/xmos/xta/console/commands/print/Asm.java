package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Asm extends Command {
	private int m_routeId;

	public Asm() {
		m_routeId = 0;
	}

	public Asm(int routeId) {
		m_routeId = routeId;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "asm";
    }        

    public String commandLine() {
    	return "asm " + getRouteId(m_routeId);
    }
    
    public String description(Engine engine, boolean detailed) {
        return "asm <route id>: Display annotated assembly for the given route";      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		
    	
    	try {
    	    return new Asm(getRouteId(args[index + 1]));
	    } catch (NumberFormatException e) {
	        return null;
	    }    	
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;
		
		AsmDisplayer displayer = new AsmDisplayer(engine, console, m_routeId); 
        displayer.display();
   } 
}
