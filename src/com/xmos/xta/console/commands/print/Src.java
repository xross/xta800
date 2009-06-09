package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Src extends Command {
	private int m_routeId;

	public Src() {
		m_routeId = 0;
	}

	public Src(int routeId) {
		m_routeId = routeId;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "src";
    }        

    public String commandLine() {
    	return "src " + getRouteId(m_routeId);
    }
    
    public String description(Engine engine, boolean detailed) {
        return "src <route id>: Display annotated source file(s) for given route";      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	try {
    		return new Src(getRouteId(args[index + 1]));
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
				
		SrcDisplayer displayer = new SrcDisplayer(engine, console, m_routeId, false);
		displayer.display();	
   }    
}
