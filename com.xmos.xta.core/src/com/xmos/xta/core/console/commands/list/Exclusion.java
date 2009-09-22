package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;

public class Exclusion extends Command {	
	public enum Operation {
		ADD, REMOVE, LIST
	};
	
	private String m_reference;
	private Operation m_operation;
	
	public Exclusion(Operation operation) {
		m_operation = operation;
	}

	public Exclusion(Operation operation, String reference) {
		m_operation = operation;
		m_reference = reference;
	}
	
    public int numArgs() {
    	switch (m_operation) {
    	case ADD:
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
    		return "exclusion"; 
    	case LIST:
    		return "exclusions";
    	}    			
    	assert(false);
        return "";    	
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
       		return "exclusion " + m_reference;
    	    
    	case LIST:
       		return "exclusions";
    	}    			
    	assert(false);
        return "";    	
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "";
    	switch (m_operation) {
    	case ADD:
        	descString = "exclusion <ANY_REF>: Adds the given reference to the list of exclusions";
        	break;
    	case REMOVE:
        	descString = "exclusion: <ANY_REF|*>: Removes the given reference (or all if '*') from the list of exclusions";
        	break;
    	case LIST:
        	descString = "exclusions: Displays the exclusions";
        	break;
    	}
    	return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		
	    
    	switch (m_operation) {
    	case ADD:    		
    	case REMOVE:
        	return new Exclusion(m_operation, args[index + 1]);

    	case LIST:
        	return new Exclusion(m_operation, "");
    	}
    	assert(false);
		return null;    						
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

    	switch (m_operation) {
    	case ADD:
    		engine.addExclusion(getSrc(), m_reference);
    		break;

    	case REMOVE:
    		engine.removeExclusion(getSrc(), m_reference);
    		break;
    		
    	case LIST:
    		listExclusions(engine, console);
    		break;
    	}
    }
    
    private void listExclusions(Engine engine, Console console) {
    	Collection<Reference> exclusions = engine.getExclusions();
        if (exclusions == null)
	    	return;
        	    
	    if (exclusions.size() > 0) {
	    	for (Reference exclusion : exclusions) {
	    	    console.display(String.format("- 0x%x: %s\n", 
	    	    		exclusion.getPc(), exclusion.getRef()));
	    	}    	    	
	    } else {
	    	console.display("none\n");
	    }
    }
 }
