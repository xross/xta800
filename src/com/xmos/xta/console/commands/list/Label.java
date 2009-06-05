package com.xmos.xta.console.commands.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class Label extends Command {
	public enum Operation {
		ADD, REMOVE, LIST
	};

	private ArrayList<String> m_labels;
	private String m_refString;
	private Operation m_operation;

	public Label(Operation operation) {
		m_operation = operation;
		m_labels = new ArrayList<String>();
		m_refString = "";
	}

	public Label(String label, String refString, Operation operation) {
		assert(operation == Operation.ADD);
		m_operation = operation;
		m_labels = new ArrayList<String>();
		m_labels.add(label);
		m_refString = refString;		
	}

	public int numArgs() {
    	switch (m_operation) {
    	case ADD:
            return 2;         
    	case REMOVE:
            return m_labels.size();
    	case LIST:
            return 0;
    	}    		
    	assert(false);
        return 0;
	}

	public String name() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return "label"; 
    	case LIST:
    		return "labels";
    	}    			
    	assert(false);
        return "";
	}

	public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    		assert(m_labels.size() == 1);
    		return "label " + m_refString + " " + m_labels.get(0); 
    	case REMOVE:
    		String commandLine = "label";
    		for (String label : m_labels) {
    			commandLine += " " + label;
    		}
    		return commandLine;
    	case LIST:
    		return "labels";
    	}    			
    	assert(false);
        return "";
	}

	public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "label <CONSTRAINT_REF> <label>: Adds the given label to the labels list";         
    	case REMOVE:
            return "label [label]+: Removes the given labels from the labels list (supports regular expressions)";
    	case LIST:
            return "labels: Lists the labels";
    	}    		
    	assert(false);
        return "";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
    	switch (m_operation) {
    	case ADD:
    		return new Label(args[index + 2], args[index + 1], m_operation);            
    	case REMOVE:
    		Label label = new Label(m_operation);
    		int i = 1;
    		while (!areArgsDone(args, index + i)) {
    			label.m_labels.add(args[index + i]);
    			i++;
    		}

    		if (label.m_labels.size() < 1)
    			return null;

    		return label;
    	case LIST:
    		return new Label(m_operation);            
    	}    						
    	assert(false);
        return null;
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

    	switch (m_operation) {
    	case ADD:
    		assert(m_labels.size() == 1);
    		Reference ref = ReferenceFactory.createReference(
    				m_refString, ReferenceClass.ANY_REF);
			engine.addLabel(m_labels.get(0), ref);
			break;			
    	case REMOVE:
    		for (String label : m_labels) {
    			engine.removeLabel(label);
    		}
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
		if (engine.getLabels().entrySet().size() == 0){
			console.display("none\n");
			return;
		}
		
    	Iterator<Map.Entry<String, Integer>> iter = engine.getLabels().entrySet().iterator();        
    	while (iter.hasNext()) {
    	    Map.Entry<String, Integer> pair = iter.next();
    	    console.display(String.format("Label: %s, pc: 0x%x\n", pair.getKey(), pair.getValue()));
    	}		
	}
}
