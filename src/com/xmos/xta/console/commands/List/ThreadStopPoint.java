package com.xmos.xta.console.commands.List;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class ThreadStopPoint extends Command {
	public enum Operation {
		ADD, REMOVE, LIST
	};

	private String m_refString;
	private Operation m_operation;

	public ThreadStopPoint(Operation operation) {
		m_operation = operation;
		m_refString = "";
	}

	public ThreadStopPoint(String refString, Operation operation) {
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
    		return "threadstoppoint"; 
    	case LIST:
    		return "threadstoppoints";
    	}    			
    	assert(false);
        return "";
	}

	public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    		return "threadstoppoint " + m_refString; 
    	case REMOVE:
    		return "threadstoppoint " + m_refString;
    	case LIST:
    		return "threadstoppoints";
        default:
        	assert(false);
            return "";
    	}    			
	}

	public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "threadstoppoint <LABEL_REF>: Adds the given reference to the thread stop points list";         
    	case REMOVE:
            return "threadstoppoint <LABEL_REF>: Removes the given from reference from the thread stop points list";
    	case LIST:
            return "threadstoppoints: Lists the thread stop points";
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
    		return new ThreadStopPoint(args[index + 1], m_operation);            
    	case LIST:
    		return new ThreadStopPoint(m_operation);            
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
			engine.addThreadStopPoint(ref);
			break;			
    	case REMOVE:
			engine.removeThreadStopPoint(ref);
			break;			
    	case LIST:
    		listThreadStopPoints(engine, console);        	
    		break;    		
        default:
           	assert(false);
       		break;
    	}
	}
	
	private void listThreadStopPoints(Engine engine, Console console) {
		if (engine.getThreadStopPoints().size() == 0){
			console.display("none\n");
			return;
		}
		
		for (Integer pc : engine.getThreadStopPoints()) {
    	    console.display(String.format("0x%x ", pc));
    	}		
	    console.display("\n");
	}
}
