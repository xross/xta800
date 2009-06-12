package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class LoopPath extends Command {	
	public enum Operation {
		SET,
		UNSET,
	}

	private Operation m_operation;
	private String m_refString;
	private int m_iterations;
	
	public LoopPath(Operation operation) {
		m_operation = operation;
		m_refString = "";
		m_iterations = 0;
	}
	
	public LoopPath(String refString, int iterations) {
		m_operation = Operation.SET;
		m_refString = refString;
		m_iterations = iterations;
	}

	public LoopPath(String refString) {
		m_operation = Operation.UNSET;
		m_refString = refString;
		m_iterations = 0;
	}
	
    public int numArgs() {
    	switch (m_operation) {
    	case SET:
    		return 2; 
    	case UNSET:
    		return 1;
    	}
    	assert(false);
        return 0;    	    	
    }        

    public String name() {
    	return "looppath";
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case SET:
        	return "looppath " + " " + m_refString + " " + m_iterations;        	
    	case UNSET:
        	return "looppath " + " " + m_refString;
    	}    			
    	assert(false);
    	return "";    	    
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case SET:
    		return "looppath <SRCLABEL_REF> <iterations>: Sets the number of iterations for the path identified";
    	case UNSET:
    		return "looppath <SRCLABEL_REF>: Clears the number of iterations for the path identified";
    	}    			
    	assert(false);
    	return "";    	    
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	switch (m_operation) {
    	case SET:
    	    int iterations;
        	try { iterations = Integer.parseInt(args[index + 2]); }
    	    catch (NumberFormatException e) {
    	        return null;
    	    }    		      	    
            return new LoopPath(args[index + 1], iterations);    		
    		
    	case UNSET:
            return new LoopPath(args[index + 1]);    		    		
    	}    			
    	assert(false);
    	return null;    	            
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

    	switch (m_operation) {
    	case SET:
    		if (!engine.setLoopPathIterations(m_refString, m_iterations)) {
        		console.display(String.format(
        				"Error: Failed to set loop path iterations on reference: %s\n", 
        				m_refString));    		
        	}
    		break;
    		
    	case UNSET:
    		if (!engine.unsetLoopPathIterations(m_refString)) {
        		console.display(String.format(
        				"Error: Failed to unset loop path iterations on reference id: %s\n", 
        				m_refString));    		
        	}
    		break;
    	}    					
    }    
 }
