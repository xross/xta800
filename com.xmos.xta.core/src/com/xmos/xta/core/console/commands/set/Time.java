package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;

public class Time extends Command {
	private int m_routeId;
	private String m_refString;
	private TimeUnit m_timeUnit;
	
	public Time() {
		m_routeId = 0;
		m_refString = null;
		m_timeUnit = null;
	}
	
	public Time(int routeId, String refString, TimeUnit timeUnit) {
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
    	return "time";
    }        

    public String commandLine() {
       	return String.format("time %s %s %s", getRouteId(m_routeId), 
       			m_refString, m_timeUnit.getDescription(false));
    }
    
    public String description(Engine engine, boolean detailed) {
        String descString = "time <route id> <ENDPOINT_REF> <value> <mode>: Sets the time taken for the instruction at the given pc";         
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
	    	return new Time(routeId, ref, timeUnit);
	    }
	    return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	    	
    	
		Node node = getReferencedNode(engine, m_routeId, m_refString);
		if (node != null) {
	        engine.setFixedTime(getSrc(), node, m_timeUnit, Type.INSTRUCTION_TIME);
			
		} else {		
	        if (!engine.setFixedTime(getSrc(), m_routeId, m_refString, m_timeUnit, Type.INSTRUCTION_TIME)) {
	   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
	   	       			"failed to set instruction time on reference id: %s", m_refString));    			            	        	
	        }
		}		
    }    
}
