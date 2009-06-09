package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Mix extends Command {
	private int m_routeId;

	public Mix() {
		m_routeId = 0;
	}

	public Mix(int routeId) {
		m_routeId = routeId;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "mix";
    }        

    public String commandLine() {
    	return "mix " + getRouteId(m_routeId);
    }
    
    public String description(Engine engine, boolean detailed) {
        return "mix <route id>: Display annotated source file(s) + assembly instructions for the given route";      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	try {
    		return new Mix(getRouteId(args[index + 1]));
    	}
    	catch (NumberFormatException e) {
    	    return null;
    	}
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;
		
		SrcDisplayer displayer = new SrcDisplayer(engine, console, m_routeId, true);
		displayer.display();
   }    
}
