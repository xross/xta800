package com.xmos.xta.console.commands;

import java.util.ArrayList;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.LabelInfo;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;

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
            return "label <ref> <label>: Adds the given label to the labels list";         
    	case REMOVE:
            return "label [label]+: Removes the given labels from the labels list (supports regular expressions)";
    	case LIST:
            return "labels: Lists the labels";
    	}    		
    	assert(false);
        return "";
	}

	public Command clone(String[] args, int index, boolean commandLineMode) {
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
    		CodeReference ref = CodeReferenceFactory.createCodeReference(m_refString);
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

		for (LabelInfo info : engine.getLabels().values()) {
    	    console.display(String.format("Label: %s, pc: 0x%x", info.getLabel(), info.getPc())); 
    	    
    	    if (!info.getCompilationDir().equals("")) {
    	    	console.display(", compilation dir: " + info.getCompilationDir());
    	    }
    	    if (!info.getCompilationDir().equals("")) {
    	    	console.display(", filename: " + info.getFileName());
    	    }
    	    if (!info.getCompilationDir().equals("")) {
    	    	console.display(", line number: " + info.getLineNumber());
    	    }
    	    console.display("\n");
    	}		
	}
}
