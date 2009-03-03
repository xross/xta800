package com.xmos.xta.console.commands;

import java.util.ArrayList;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.SrcLabelInfo;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class SrcLabel extends Command {
	public enum Operation {
		ADD, REMOVE, LIST
	};

	private ArrayList<String> m_labels;
	private String m_refString;
	private Operation m_operation;

	public SrcLabel(Operation operation) {
		m_operation = operation;
		m_labels = new ArrayList<String>();
		m_refString = "";
	}

	public SrcLabel(String label, String refString, Operation operation) {
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
    		return "srclabel"; 
    	case LIST:
    		return "srclabels";
    	}    			
    	assert(false);
        return "";
	}

	public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    		assert(m_labels.size() == 1);
    		return "srclabel " + m_refString + " " + m_labels.get(0); 
    	case REMOVE:
    		String commandLine = "srclabel";
    		for (String label : m_labels) {
    			commandLine += " " + label;
    		}
    		return commandLine;
    	case LIST:
    		return "srclabels";
    	}    			
    	assert(false);
        return "";
	}

	public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "srclabel <SRCLABEL_REF> <label>: Adds the given source label to the source labels list";         
    	case REMOVE:
            return "srclabel [label]+: Removes the given source labels from the source labels list (supports regular expressions)";
    	case LIST:
            return "srclabels: Lists the source labels";
    	}    		
    	assert(false);
        return "";
	}

	public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length)
			return null;
				
    	switch (m_operation) {
    	case ADD:
    		return new SrcLabel(args[index + 2], args[index + 1], m_operation);            
    	case REMOVE:
    		SrcLabel srclabel = new SrcLabel(m_operation);
    		int i = 1;
    		while (!areArgsDone(args, index + i)) {
    			srclabel.m_labels.add(args[index + i]);
    			i++;
    		}

    		if (srclabel.m_labels.size() < 1)
    			return null;

    		return srclabel;
    	case LIST:
    		return new SrcLabel(m_operation);            
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
    				m_refString, ReferenceClass.SRCLABEL_REF);
			engine.addSrcLabel(m_labels.get(0), ref);
			break;			
    	case REMOVE:
    		for (String label : m_labels) {
    			engine.removeSrcLabel(label);
    		}
			break;			
    	case LIST:
        	listSrcLabels(engine, console);        	
    		break;    		
        default:
           	assert(false);
       		break;
    	}
	}
	
	private void listSrcLabels(Engine engine, Console console) {
		if (engine.getSrcLabels().entrySet().size() == 0){
			console.display("none\n");
			return;
		}

		for (SrcLabelInfo info : engine.getSrcLabels().values()) {
    	    console.display(String.format("Label: %s, pc: 0x%x", info.getLabel(), info.getPc())); 
    	    
    	    if (!info.getCompilationDir().equals("")) {
    	    	console.display(", compilation dir: " + info.getCompilationDir());
    	    	console.display(", filename: " + info.getFileName());
    	    	console.display(", line number: " + info.getLineNumber());
    	    }
    	    console.display("\n");
    	}		
	}
}
