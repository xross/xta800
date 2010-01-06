package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;

public class Required extends Command {
	private int m_routeId;
	private String m_value;
	private String m_mode;
	
	public Required() {
		m_routeId = 0;
	}
	
	public Required(int routeId, String value, String mode) {
		m_routeId = routeId;
		m_value = value;
		m_mode = mode;
	}

    public int numArgs() {
   		return 3; 
    }

    public String name() {
    	return "required";
    }        

    public String commandLine() {
       	return String.format("required %s %s %s", getRouteId(m_routeId), 
       			m_value, m_mode);
    }
    
    public String description(Engine engine, boolean detailed) {
        String descString = "required <route id> <value> <mode>: Sets the maximum allowed time taken for the given route";         
      	if (detailed) { 
           	descString += "\n" + TimeUnit.getHelp();
       	}
       	return descString;
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length) 
			return null;
    	
       	int routeId = 0;
   		try {
     		routeId = getRouteId(args[index + 1]);
    	} catch (NumberFormatException e) {
    		return null;
    	} 	
    	
  		String value = args[index + 2];
     	String mode = args[index + 3];

	    TimeUnit timeUnit = new TimeUnit(value, mode);
	    if (timeUnit.isValid()) {
	    	return new Required(routeId, value, mode);
	    }
	    return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;    	
    	
	    TimeUnit timeUnit = new TimeUnit(m_value, m_mode);
	    assert(timeUnit.isValid());
        engine.setRequiredTime(getSrc(), m_routeId, timeUnit);
    }    
}
