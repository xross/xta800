package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class LoopMax extends Command {	
	public enum Operation {
		SET,
		UNSET,
	}
	
	private Operation m_operation;
	private String m_refString;
	private int m_iterations;
	
	public LoopMax(Operation operation) {
		m_operation = operation;
		m_iterations = 0;
		m_refString = "";
	}
	
	public LoopMax(String refString, int iterations) {
		m_operation = Operation.SET;
		m_refString = refString;
		m_iterations = iterations;
	}

	public LoopMax(String refString) {
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
    	return "loopmax";
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case SET:
        	return "loopmax " + " " + m_refString + " " + m_iterations;        	
    	case UNSET:
        	return "loopmax " + " " + m_refString;
    	}    			
    	assert(false);
    	return "";    	    
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case SET:
    		return "loopmax <SRCLABEL_REF> <iterations>: Sets the maximum number of iterations for the loop identified";
    	case UNSET:
    		return "loopmax <SRCLABEL_REF>: Clears the maximum number of iterations for the loop identified";
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
            return new LoopMax(args[index + 1], iterations);
    		
    	case UNSET:
            return new LoopMax(args[index + 1]);
    	}    			
    	assert(false);
    	return null;    	                
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;				
		
    	switch (m_operation) {
    	case SET:
    		if (!engine.setLoopMaxIterations(m_refString, m_iterations)) {
        		console.display(String.format(
        				"Error: Failed to set max loop iterations on reference: %s\n", 
        				m_refString));    		
        	}
    		break;
    		
    	case UNSET:
    		if (!engine.unsetLoopMaxIterations(m_refString)) {
        		console.display(String.format(
        				"Error: Failed to unset max loop iterations on reference: %d\n", 
        				m_refString));    		
        	}
    		break;
    	}    							
    }    
 }
