package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;

public class PathTime extends Command {
	private int m_routeId;	
	private String m_fromRef;
	private String m_toRef;
	private TimeUnit m_timeUnit;
	
	public PathTime() {
		m_routeId = 0;
		m_fromRef = "";
		m_toRef = "";
		m_timeUnit = null;
	}
	
	public PathTime(int routeId, String fromRef, String toRef, TimeUnit timeUnit) {
		m_routeId = routeId;
		m_fromRef = fromRef;
		m_toRef = toRef;	  	
		m_timeUnit = timeUnit;
	}
	
    public int numArgs() {
    	return 5;
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.ENDPOINT, ArgType.ENDPOINT};
	}    

    public String name() {
    	return "pathtime";
    }        

    public String commandLine() {
    	return "pathtime " + getRouteId(m_routeId) + " " + m_fromRef + " " + m_toRef + " " + m_timeUnit.getDescription(false);
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "pathtime <route id> <from ENDPOINT_REF> <to ENDPOINT_REF> <value> <mode>: Set timing requirement for the given path on the given route";      	
       	if (detailed) {    	    	
       		descString += "\n" + TimeUnit.getHelp();
       	}
        return descString;    
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

		try {			
			int routeId    = getRouteId(args[index + 1]);		
    	    String fromRef = args[index + 2];
            String toRef   = args[index + 3];
    	    String value   = args[index + 4];
   		    String mode    = args[index + 5];

	        TimeUnit timeUnit = new TimeUnit(value, mode);
	        if (timeUnit.isValid()) { 
	    	    return new PathTime(routeId, fromRef, toRef, timeUnit);
	        }
	    } catch (NumberFormatException e) {}
	    return null;                
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        engine.setPathTime(getSrc(), m_routeId, m_fromRef, m_toRef, m_timeUnit);
    }    
 }
