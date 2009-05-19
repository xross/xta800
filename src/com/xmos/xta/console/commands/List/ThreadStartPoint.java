package com.xmos.xta.console.commands.List;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class ThreadStartPoint extends Command {
	public enum Operation {
		ADD, REMOVE, LIST
	};

	private String m_refString;
	private Operation m_operation;

	public ThreadStartPoint(Operation operation) {
		m_operation = operation;
		m_refString = "";
	}

	public ThreadStartPoint(String refString, Operation operation) {
		m_operation = operation;
		m_refString = refString;
	}
	
	public int numArgs() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
            return 1;
    	case LIST:
            return 0;
        default:
        	assert(false);
            return 0;
    	}    		
	}

	public String name() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return "threadstartpoint"; 
    	case LIST:
    		return "threadstartpoints";
    	}    			
    	assert(false);
        return "";
	}

	public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    		return "threadstartpoint " + m_refString; 
    	case REMOVE:
    		return "threadstartpoint " + m_refString;
    	case LIST:
    		return "threadstartpoints";
        default:
        	assert(false);
            return "";
    	}    			
	}

	public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "threadstartpoint <LABEL_REF>: Adds the given reference to the thread start points list";         
    	case REMOVE:
            return "threadstartpoint <LABEL_REF>: Removes the given from reference from the thread start points list";
    	case LIST:
            return "threadstartpoints: Lists the thread start points";
        default:
        	assert(false);
            return "";
    	}    		
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return new ThreadStartPoint(args[index + 1], m_operation);            
    	case LIST:
    		return new ThreadStartPoint(m_operation);            
        default:
        	assert(false);
            return null;
    	}    						
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Reference ref = ReferenceFactory.createReference(
				m_refString, ReferenceClass.LABEL_REF);

    	switch (m_operation) {
    	case ADD:
			engine.addThreadStartPoint(ref);
			break;			
    	case REMOVE:
			engine.removeThreadStartPoint(ref);
			break;			
    	case LIST:
    		listThreadStartPoints(engine, console);        	
    		break;    		
        default:
           	assert(false);
       		break;
    	}
	}
	
	private void listThreadStartPoints(Engine engine, Console console) {
		if (engine.getThreadStartPoints().size() == 0){
			console.display("none\n");
			return;
		}
		
		for (Integer pc : engine.getThreadStartPoints()) {
    	    console.display(String.format("0x%x ", pc));
    	}		
	    console.display("\n");
	}
}
