package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Trace extends Command {
	private int m_routeId;

	public Trace() {
		m_routeId = 0;
	}
	
	public Trace(int routeId) {
		m_routeId = routeId;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "trace";
    }        

    public String commandLine() {
    	return "trace " + getRouteId(m_routeId);
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("trace <route/node id>: Display instruction trace for the worst case path of the given route/node");
    	return descString;    	                
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	try {
    	    return new Trace(getRouteId(args[index + 1]));
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
				
		TraceDisplayer displayer = new TraceDisplayer(engine, console, m_routeId);	
		displayer.display();
    }    
}
