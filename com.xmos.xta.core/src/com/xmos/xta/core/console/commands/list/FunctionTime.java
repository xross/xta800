package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.list.List.Operation;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.assertions.FunctionTimeInfo;

public class FunctionTime extends Command {
	private Operation m_operation;	
	private String m_functionName;
	private TimeUnit m_timeUnit;

	public FunctionTime(Operation operation) {
		m_operation = operation;
		m_functionName = "";
	}
	
	public FunctionTime(Operation operation, String functionName, TimeUnit timeUnit) {
		m_operation = operation;
		m_functionName = functionName;
		m_timeUnit = timeUnit;
	}
	
    public int numArgs() {
    	switch (m_operation) {
    	case ADD:
       		return 3; 
    		
    	case REMOVE:
       		return 1;
        	
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
    		return new Command.ArgType[] {ArgType.FUNCTION};
    		
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
    		return "functiontime"; 
    	case LIST:
    		return "functiontimes";
    	}    			
    	assert(false);
        return "";    	
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case ADD:
           	return "functiontime " + m_functionName + " " + m_timeUnit.getDescription(false);
           	
    	case REMOVE:
           	return "functiontime " + m_functionName;        	
    	    
    	case LIST:
       		return "functiontimes";
    	}    			
    	assert(false);
        return "";    	        
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "";
    	switch (m_operation) {
    	case ADD:
    		descString = "functiontime <FUNCTION_REF> <value> <mode>: Adds the given function time to the list of assertions";
           	if (detailed) {    	    	
           		descString += "\n" + TimeUnit.getHelp();
           	}
        	break;
    	case REMOVE:
    		descString = "functiontime <FUNCTION_REF|*>: Removes the given functon time from the list of assertions";
        	break;
    	case LIST:
        	descString = "functiontimes: Displays the function time assertions";
        	break;
    	}
    	return descString;   		    	            
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	switch (m_operation) {
    	case ADD:
    	    TimeUnit timeUnit = new TimeUnit(args[index + 2], args[index + 3]);
    	    if (timeUnit.isValid()) {
    	    	return new FunctionTime(m_operation, args[index + 1], timeUnit);
    	    }
    	    return null;
    		
    	case REMOVE:
        	return new FunctionTime(m_operation, args[index + 1], null);

    	case LIST:
        	return new FunctionTime(m_operation);
    	}
    	assert(false);
		return null;				    
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

    	switch (m_operation) {
    	case ADD:
    		engine.addFunctionTime(getSrc(), m_functionName, m_timeUnit);
    		break;

    	case REMOVE:
    		engine.removeFunctionTime(getSrc(), m_functionName);
    		break;
    		
    	case LIST:
    		Collection<FunctionTimeInfo> infos = engine.getFunctionTimeAssertions();
    	    if (infos.isEmpty()) {
    	    	console.display("none\n");
    	    	return;
    	    }
    		for (FunctionTimeInfo info : infos) {
    			console.display(info + "\n");
    		}	    
    		break;
    	} 		
    }
}
