package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.list.List.Operation;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.assertions.PathTimeInfo;

public class PathTime extends Command {
	private Operation m_operation;	
	private String m_fromRef;
	private String m_toRef;
	private TimeUnit m_timeUnit;

	public PathTime(Operation operation) {
		m_operation = operation;
		m_fromRef = "";
		m_toRef = "";
	}
	
	public PathTime(Operation operation, String fromRef, String toRef, TimeUnit timeUnit) {
		m_operation = operation;
		m_fromRef = fromRef;
		m_toRef = toRef;	  	
		m_timeUnit = timeUnit;
	}
	
    public int numArgs() {
    	switch (m_operation) {
    	case ADD:
       		return 4; 
    		
    	case REMOVE:
       		return 2;
        	
    	case LIST:
       		return 0;
    	}
    	assert(false);
    	return 0;    	
    }        

	public ArgType[] getArgTypes() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return new Command.ArgType[] {ArgType.ENDPOINT, ArgType.ENDPOINT};
    		
    	case LIST:
            return super.getArgTypes();            
    	}    		
    	assert(false);
        return null;
	}    

    public String name() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return "pathtime"; 
    	case LIST:
    		return "pathtimes";
    	}    			
    	assert(false);
        return "";    	
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case ADD:
           	return "pathtime " + m_fromRef + " " + m_toRef + " " + m_timeUnit.getDescription(false);
           	
    	case REMOVE:
           	return "pathtime " + m_fromRef + " " + m_toRef;        	
    	    
    	case LIST:
       		return "pathtimes";
    	}    			
    	assert(false);
        return "";    	        
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "";
    	switch (m_operation) {
    	case ADD:
    		descString = "pathtime <from ENDPOINT_REF> <to ENDPOINT_REF> <value> <mode>: Adds the given path time to the list of assertions";
           	if (detailed) {    	    	
           		descString += "\n" + TimeUnit.getHelp();
           	}
        	break;
    	case REMOVE:
    		descString = "pathtime <from ENDPOINT_REF> <to ENDPOINT_REF>: Removes the given path time from the list of assertions";
        	break;
    	case LIST:
        	descString = "pathtimes: Displays the path time assertions";
        	break;
    	}
    	return descString;   		    	        
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		
		
    	switch (m_operation) {
    	case ADD:
    	    TimeUnit timeUnit = new TimeUnit(args[index + 3], args[index + 4]);
    	    if (timeUnit.isValid()) {
    	    	return new PathTime(m_operation, args[index + 1], args[index + 2], timeUnit);
    	    }
    	    return null;
    		
    	case REMOVE:
        	return new PathTime(m_operation, args[index + 1], args[index + 2], null);

    	case LIST:
        	return new PathTime(m_operation);
    	}
    	assert(false);
		return null;				
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

    	switch (m_operation) {
    	case ADD:
    		engine.addPathTime(getSrc(), m_fromRef, m_toRef, m_timeUnit);
    		break;

    	case REMOVE:
    		engine.removePathTime(getSrc(), m_fromRef, m_toRef);
    		break;
    		
    	case LIST:
    		Collection<PathTimeInfo> infos = engine.getPathTimeAssertions();
    	    if (infos.isEmpty()) {
    	    	console.display("none\n");
    	    	return;
    	    }
    		for (PathTimeInfo info : infos) {
    			console.display(info + "\n");
    		}	    
    		break;
    	} 		
    }    
 }
