package com.xmos.xta.console.commands;

import java.util.Iterator;
import java.util.Map;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;

public class Label extends Command {
	enum Operation {
		ADD, REMOVE, LIST
	};

	private String m_label;
	private CodeReference m_ref;
	private Operation m_operation;

	Label(Operation operation) {
		m_operation = operation;
		m_label = null;
		m_ref = null;
	}

	Label(String label, CodeReference ref, Operation operation) {
		assert(operation == Operation.ADD);
		m_operation = operation;
		m_label = label;
		m_ref = ref;
	}

	Label(String label, Operation operation) {
		assert(operation == Operation.REMOVE);
		m_operation = operation;
		m_label = label;
		m_ref = null;
	}
	
	public int numArgs() {
    	switch (m_operation) {
    	case ADD:
            return 2;         
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
		return "label";
	}

	public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    		return "label " + m_ref.getRef() + " " + m_label; 
    	case REMOVE:
    		return "label " + m_label;
    	case LIST:
    		return "label";
        default:
        	assert(false);
            return "";
    	}    			
	}

	public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "label <ref> <label>: Adds the given label/reference to the labels list";         
    	case REMOVE:
            return "label <label>: Removes the given label from the labels list";
    	case LIST:
            return "label: Lists the labels";
        default:
        	assert(false);
            return "";
    	}    		
	}

	public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length)
			return null;
				
    	switch (m_operation) {
    	case ADD:
    		CodeReference ref = CodeReferenceFactory.createCodeReference(args[index + 2]);
    		return new Label(args[index + 1], ref, m_operation);            
    	case REMOVE:
    		return new Label(args[index + 1], m_operation);            
    	case LIST:
    		return new Label(m_operation);            
        default:
        	assert(false);
            return null;
    	}    						
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;
		
    	switch (m_operation) {
    	case ADD:
			if (!engine.addLabel(m_label, m_ref)) {
    			console.display("Error: could not resolve reference: " + m_ref);
    		}		        	        				
			break;			
    	case REMOVE:
			engine.removeLabel(m_label);
			break;			
    	case LIST:
        	listLabels(engine, console);        	
    		break;    		
        default:
           	assert(false);
       		break;
    	}
	}
	
	private void listLabels(Engine engine, Console console) {
    	Iterator<Map.Entry<String, Integer>> iter = engine.getLabels().entrySet().iterator();        
    	while (iter.hasNext()) {
    	    Map.Entry<String, Integer> pair = iter.next();
    	    console.display(String.format("- ref: %s, pc: 0x%x\n", pair.getKey(), pair.getValue()));
    	}		
	}
}
