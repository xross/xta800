package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.list.List.Operation;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.defines.LoopInfo;

public class Loop extends Command {	
	private Operation m_operation;
	private String m_refString;
	private int m_iterations;
	
	public Loop(Operation operation) {
		m_operation = operation;
		m_refString = "";
		m_iterations = 0;
	}
	
	public Loop(Operation operation, String refString, int iterations) {
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
    		return "loop"; 
    	case LIST:
    		return "loops";
    	}    			
    	assert(false);
        return "";    	
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case ADD:
           	return "loop " + " " + m_refString + " " + m_iterations;
           	
    	case REMOVE:
           	return "loop " + " " + m_refString;        	
    	    
    	case LIST:
       		return "loops";
    	}    			
    	assert(false);
        return "";    	    
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "";
    	switch (m_operation) {
    	case ADD:
    		descString = "loop <ANY_REF> <iterations>: Adds the given loop count define to the list of defines";
        	break;
    	case REMOVE:
    		descString = "loop <ANY_REF|*>: Removes the given loop count define to the list of defines";
        	break;
    	case LIST:
        	descString = "loops: Displays the loop defines";
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
    	        return new Loop(m_operation, args[index + 1], iterations);    		
    	        
    		} catch (NumberFormatException e) {
    	   	    return null;
    	   	}    		   	    		
    		
    	case REMOVE:
        	return new Loop(m_operation, args[index + 1], 0);

    	case LIST:
        	return new Loop(m_operation);
    	}
    	assert(false);
		return null;		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
	    
    	switch (m_operation) {
    	case ADD:
    		engine.addLoop(getSrc(), m_refString, m_iterations);
    		break;

    	case REMOVE:
    		engine.removeLoop(getSrc(), m_refString);
    		break;
    		
    	case LIST:
    		Collection<LoopInfo> infos = engine.getLoopDefines();
    	    if (infos.isEmpty()) {
    	    	console.display("none\n");
    	    	return;
    	    }
    		for (LoopInfo info : infos) {
    			console.display(info + "\n");
    		}	    
    		break;
    	}    
    }
 }