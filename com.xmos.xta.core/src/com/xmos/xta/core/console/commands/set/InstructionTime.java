package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.TimeUnit;

public class InstructionTime extends Command {
	private int m_routeId;
	private String m_refString;
	private TimeUnit m_timeUnit;
	
	public InstructionTime() {
		m_routeId = 0;
		m_refString = null;
		m_timeUnit = null;
	}
	
	public InstructionTime(int routeId, String refString, TimeUnit timeUnit) {
		m_routeId = routeId;
		m_refString = refString;
		m_timeUnit = timeUnit;
	}

    public int numArgs() {
   		return 4; 
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.ENDPOINT};
	}    
    
    public String name() {
    	return "instructiontime";
    }        

    public String commandLine() {
       	return String.format("instructiontime %s %s %s", getRouteId(m_routeId), 
       			m_refString, m_timeUnit.getDescription(false));
    }
    
    public String description(Engine engine, boolean detailed) {
        String descString = "instructiontime <route id> <ENDPOINT_REF> <value> <mode>: Sets the time taken for the instruction at the given pc";         
       	if (detailed) {    	    	
           	descString += "\n";
           	descString += TimeUnit.getHelp();
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

    	String ref = args[index + 2];   		
    	String value = args[index + 3];
   		String mode = args[index + 4];
   		    
	    TimeUnit timeUnit = new TimeUnit(value, mode);
	    if (timeUnit.isValid()) {
	    	return new InstructionTime(routeId, ref, timeUnit);
	    }
	    return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	    	
    	
        if (!engine.setInstructionTime(getSrc(), m_routeId, m_refString, m_timeUnit)) {
   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
   	       			"failed to set instruction time on reference id: %s", m_refString));    			            	        	
		}		
    }    
}
