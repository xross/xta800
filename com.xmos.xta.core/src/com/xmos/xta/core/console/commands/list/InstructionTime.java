package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.list.List.Operation;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.assertions.InstructionTimeInfo;

public class InstructionTime extends Command {
	private Operation m_operation;	
	private String m_refString;
	private TimeUnit m_timeUnit;

	public InstructionTime(Operation operation) {
		m_operation = operation;
		m_refString = null;
		m_timeUnit = null;
	}
	
	public InstructionTime(Operation operation, String refString, TimeUnit timeUnit) {
		m_operation = operation;
		m_refString = refString;
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
    		return new Command.ArgType[] {ArgType.ENDPOINT};
    		
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
    		return "instructiontime"; 
    	case LIST:
    		return "instructiontimes";
    	}    			
    	assert(false);
        return "";    	
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case ADD:
           	return "instructiontime " + m_refString + " " + m_timeUnit.getDescription(false);
           	
    	case REMOVE:
           	return "instructiontime " + m_refString;        	
    	    
    	case LIST:
       		return "instructiontimes";
    	}    			
    	assert(false);
        return "";    	        
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "";
    	switch (m_operation) {
    	case ADD:
    		descString = "instructiontime <ENDPOINT_REF> <value> <mode>: Adds the given instruction time to the list of assertions";
           	if (detailed) {    	    	
           		descString += "\n" + TimeUnit.getHelp();
           	}
        	break;
    	case REMOVE:
    		descString = "instructiontime <ENDPOINT_REF|*>: Removes the given instruction time from the list of assertions";
        	break;
    	case LIST:
        	descString = "instructiontime: Displays the instruction time assertions";
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
    	    	return new InstructionTime(m_operation, args[index + 1], timeUnit);
    	    }
    	    return null;
    		
    	case REMOVE:
        	return new InstructionTime(m_operation, args[index + 1], null);

    	case LIST:
        	return new InstructionTime(m_operation);
    	}
    	assert(false);
		return null;				    
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	    	

    	switch (m_operation) {
    	case ADD:
    		engine.addInstructionTime(getSrc(), m_refString, m_timeUnit);
    		break;

    	case REMOVE:
    		engine.removeInstructionTime(getSrc(), m_refString);
    		break;
    		
    	case LIST:
    		Collection<InstructionTimeInfo> infos = engine.getInstructionTimeAssertions();
    	    if (infos.isEmpty()) {
    	    	console.display("none\n");
    	    	return;
    	    }
    		for (InstructionTimeInfo info : infos) {
    			console.display(info + "\n");
    		}	    
    		break;
    	} 				
    }    
}
