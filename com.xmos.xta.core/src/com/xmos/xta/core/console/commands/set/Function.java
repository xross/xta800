package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;

public class Function extends Command {
	private int m_routeId;
	private String m_functionName;
	private TimeUnit m_timeUnit;
	
	public Function() {
		m_routeId = 0;
		m_functionName = "";
		m_timeUnit = null;
	}
	
	public Function(int routeId, String functionName, TimeUnit timeUnit) {
		m_routeId = routeId;
		m_functionName = functionName;
		m_timeUnit = timeUnit;
	}
	
    public int numArgs() {
    	return 4;
    }        
	
	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.FUNCTION};
	}    
    
    public String name() {
    	return "function";
    }        

    public String commandLine() {
    	return "function " + getRouteId(m_routeId) + " " + m_functionName + " " + m_timeUnit.getDescription(false);
    }

    public String description(Engine engine, boolean detailed) {
        String descString = "function <route id> <FUNCTION_REF> <value> <mode>: Set timing requirement for the given function on the given route";         
       	if (detailed) {    	    	
       		descString += "\n" + TimeUnit.getHelp();
       	}
        return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

		try {			
			int routeId = getRouteId(args[index + 1]);
	    	String name  = args[index + 2];
	    	String value = args[index + 3];
	   		String mode  = args[index + 4];
	   		
		    TimeUnit timeUnit = new TimeUnit(value, mode);
		    if (timeUnit.isValid()) {
		    	return new Function(routeId, name, timeUnit);
		    }
	    } catch (NumberFormatException e) {}
	    return null;        
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        engine.setFunction(getSrc(), m_routeId, m_functionName, m_timeUnit); 
    }
}
