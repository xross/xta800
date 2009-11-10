package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.list.List.Operation;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.assertions.LoopPathInfo;

public class LoopPath extends Command {
	private Operation m_operation;	
	private String m_refString;
	private int m_iterations;

	public LoopPath(Operation operation) {
		m_operation = operation;
		m_refString = "";
		m_iterations = 0;
	}
	
	public LoopPath(Operation operation, String refString, int iterations) {
		m_operation = operation;
		m_refString = refString;
		m_iterations = iterations;
	}

    public int numArgs() {
    	switch (m_operation) {
    	case ADD:
       		return 2; 
    		
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
    		return new Command.ArgType[] {ArgType.ALLREFS};
    		
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
    		return "looppath"; 
    	case LIST:
    		return "looppaths";
    	}    			
    	assert(false);
        return "";    	
    }        
    
    public String commandLine() {
    	switch (m_operation) {
    	case ADD:
           	return "looppath " + " " + m_refString + " " + m_iterations;
           	
    	case REMOVE:
           	return "looppath " + " " + m_refString;        	
    	    
    	case LIST:
       		return "looppaths";
    	}    			
    	assert(false);
        return "";    	    
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "";
    	switch (m_operation) {
    	case ADD:
    		descString = "looppath <ANY_REF> <iterations>: Adds the given loop path count assertion to the list of assertions";
        	break;
    	case REMOVE:
    		descString = "looppath <ANY_REF|*>: Removes the given loop path count assertion to the list of assertions";
        	break;
    	case LIST:
        	descString = "looppaths: Displays the loop path assertions";
        	break;
    	}
    	return descString;   		
    }
    
    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	switch (m_operation) {
    	case ADD:
    		try {
    	   	    int iterations = Integer.parseInt(args[index + 2]);
    	        return new LoopPath(m_operation, args[index + 1], iterations);    		
    	        
    		} catch (NumberFormatException e) {
    	   	    return null;
    	   	}    		   	    		
    		
    	case REMOVE:
        	return new LoopPath(m_operation, args[index + 1], 0);

    	case LIST:
        	return new LoopPath(m_operation);
    	}
    	assert(false);
		return null;		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

    	switch (m_operation) {
    	case ADD:
    		engine.addLoopPath(getSrc(), m_refString, m_iterations);
    		break;

    	case REMOVE:
    		engine.removeLoopPath(getSrc(), m_refString);
    		break;
    		
    	case LIST:
    		Collection<LoopPathInfo> infos = engine.getLoopPathAssertions();
    	    if (infos.isEmpty()) {
    	    	console.display("none\n");
    	    	return;
    	    }
    		for (LoopPathInfo info : infos) {
    			console.display(info + "\n");
    		}	    
    		break;
    	}        
    }    
}
