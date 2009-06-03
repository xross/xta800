package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class Loop extends Command {	
	public enum Operation {
		SET,
		UNSET,
	}

	private Operation m_operation;
	private int m_routeId;
	private String m_refString;
	private int m_iterations;
	
	public Loop(Operation operation) {
		m_operation = operation;
		m_routeId = 0;
		m_refString = "";
		m_iterations = 0;
	}
	
	public Loop(int routeId, String refString, int iterations) {
		m_operation = Operation.SET;
		m_routeId = routeId;
		m_refString = refString;
		m_iterations = iterations;
	}

	public Loop(int routeId, String refString) {
		m_operation = Operation.UNSET;
		m_routeId = routeId;
		m_refString = refString;
		m_iterations = 0;
	}
	
    public int numArgs() {
    	switch (m_operation) {
    	case SET:
    		return 3; 
    	case UNSET:
    		return 2;
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
        	return "loop " + getRouteId(m_routeId) + " " + m_refString + " " + m_iterations;        	
    	case UNSET:
        	return "loop " + getRouteId(m_routeId) + " " + m_refString;
    	}    			
    	assert(false);
    	return "";    	    
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case SET:
    		return "loop <route id> <SRCLABEL_REF> <iterations>: Sets the number of iterations for the path identified";
    	case UNSET:
    		return "loop <route id> <SRCLABEL_REF>: Clears the number of iterations for the path identified";
    	}    			
    	assert(false);
    	return "";    	    
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

	    int routeId;
    	try { routeId = getRouteId(args[index + 1]); }
	    catch (NumberFormatException e) {
	        return null;
	    }	    	            
        
    	switch (m_operation) {
    	case SET:
    	    int iterations;
        	try { iterations = Integer.parseInt(args[index + 3]); }
    	    catch (NumberFormatException e) {
    	        return null;
    	    }    		      	    
            return new Loop(routeId, args[index + 2], iterations);    		
    		
    	case UNSET:
            return new Loop(routeId, args[index + 2]);    		    		
    	}    			
    	assert(false);
    	return null;    	            
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (engine.getRoute(m_routeId) == null){
			console.display(String.format("Error: Failed to set loop iterations: no such route id: %d\n", m_routeId));   
			return;
		}

		Reference ref = ReferenceFactory.createReference(m_refString, ReferenceClass.SRCLABEL_REF);
		if (!ref.resolve(engine, engine.getNodeId(), engine.getCoreId())) {
			console.display("Invalid code reference '" + m_refString + "'");
			return;
		}
						
    	switch (m_operation) {
    	case SET:
    		if (!engine.setLoopIterations(m_routeId, ref.getPc(), m_iterations)) {
        		console.display(String.format("Error: Failed to set loop iterations: route id: %d\n", m_routeId));    		
        	}
    		break;
    		
    	case UNSET:
    		if (!engine.unsetLoopIterations(m_routeId, ref.getPc())) {
        		console.display(String.format("Error: Failed to unset loop iterations: route id: %d\n", m_routeId));    		
        	}
    		break;
    	}    					
    }    
 }
