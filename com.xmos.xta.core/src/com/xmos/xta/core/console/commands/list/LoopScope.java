package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.list.List.Operation;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.defines.LoopScopeInfo;

public class LoopScope extends Command {	
	private Operation m_operation;	
	private String m_refString;
	private boolean m_absolute;

	public LoopScope(Operation operation) {
		m_operation = operation;
		m_refString = "";
		m_absolute = false;
	}
	
	public LoopScope(Operation operation, String refString, boolean absolute) {
		m_operation = operation;
		m_refString = refString;
		m_absolute = absolute;
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
    		return "loopscope"; 
    	case LIST:
    		return "loopscopes";
    	}    			
    	assert(false);
        return "";    	
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case ADD:
           	return "loopscope " + " " + m_refString + " " + (m_absolute ? "absolute" : "relative");
           	
    	case REMOVE:
           	return "loopscope " + " " + m_refString;        	
    	    
    	case LIST:
       		return "loopscopes";
    	}    			
    	assert(false);
        return "";    	        
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "";
    	switch (m_operation) {
    	case ADD:
    		descString = "loopscope <ANY_REF> <scope>: Adds the given loop scope define to the list of defines";
        	if (detailed) {
            	descString += "\n";
        		descString += "          scope: relative/r: Iteration number propagates to the enclosing path (Default)\n";                
        		descString += "          scope: absolute/a: Absolute number of iterations";
        	}
        	break;
    	case REMOVE:
    		descString = "loopscope <ANY_REF|*>: Removes the given loop scope define to the list of defines";
        	break;
    	case LIST:
        	descString = "loopscopes: Displays the loop scope defines";
        	break;
    	}
    	return descString;   		    	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	switch (m_operation) {
    	case ADD:
        	boolean absolute;
    	    if ((args[index + 2].compareTo("relative") == 0) ||  
    	    	(args[index + 2].compareTo("r") == 0)){
    	    	absolute = false;
    	    }
    	    else if ((args[index + 2].compareTo("absolute") == 0) ||  
    		         (args[index + 2].compareTo("a") == 0)){
    	    	absolute = true;
    	    }
    	    else {
    	    	return null;
    	    }    		 	    
            return new LoopScope(m_operation, args[index + 1], absolute);
    		
    	case REMOVE:
        	return new LoopScope(m_operation, args[index + 1], false);

    	case LIST:
        	return new LoopScope(m_operation);
    	}
    	assert(false);
		return null;				
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;		
		
    	switch (m_operation) {
    	case ADD:
    		engine.addLoopScope(getSrc(), m_refString, m_absolute);
    		break;

    	case REMOVE:
    		engine.removeLoopScope(getSrc(), m_refString);
    		break;
    		
    	case LIST:
    		Collection<LoopScopeInfo> infos = engine.getLoopScopeDefines();
    	    if (infos.isEmpty()) {
    	    	console.display("none\n");
    	    	return;
    	    }
    		for (LoopScopeInfo info : infos) {
    			console.display(info + "\n");
    		}	    
    		break;
    	} 		
    }    
 }