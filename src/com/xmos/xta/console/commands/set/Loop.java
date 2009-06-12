package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Loop extends Command {	
	public enum Operation {
		SET,
		UNSET,
	}

	private Operation m_operation;
	private String m_refString;
	private int m_iterations;
	
	public Loop(Operation operation) {
		m_operation = operation;
		m_refString = "";
		m_iterations = 0;
	}
	
	public Loop(String refString, int iterations) {
		m_operation = Operation.SET;
		m_refString = refString;
		m_iterations = iterations;
	}

	public Loop(String refString) {
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
    	return "loop";
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case SET:
        	return "loop " + " " + m_refString + " " + m_iterations;        	
    	case UNSET:
        	return "loop " + " " + m_refString;
    	}    			
    	assert(false);
    	return "";    	    
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case SET:
    		return "loop <SRCLABEL_REF> <iterations>: Sets the number of iterations for the loop identified";
    	case UNSET:
    		return "loop <SRCLABEL_REF>: Clears the number of iterations for the loop identified";
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
            return new Loop(args[index + 1], iterations);    		
    		
    	case UNSET:
            return new Loop(args[index + 1]);    		    		
    	}    			
    	assert(false);
    	return null;    	            
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

    	switch (m_operation) {
    	case SET:
    		if (!engine.setLoopIterations(m_refString, m_iterations)) {
        		console.display(String.format(
        				"Error: Failed to set loop iterations on reference: %s\n", 
        				m_refString));    		
        	}
    		break;
    		
    	case UNSET:
    		if (!engine.unsetLoopIterations(m_refString)) {
        		console.display(String.format(
        				"Error: Failed to unset loop iterations on reference id: %s\n", 
        				m_refString));    		
        	}
    		break;
    	}    					
    }    
 }
