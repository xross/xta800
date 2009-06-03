package com.xmos.xta.console.commands.list;

import java.util.ArrayList;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.engine.sections.CodePointInfo;

public class Call extends Command {
	public enum Operation {
		ADD, REMOVE, LIST
	};

	private ArrayList<String> m_calls;
	private String m_refString;
	private Operation m_operation;

	public Call(Operation operation) {
		m_operation = operation;
		m_calls = new ArrayList<String>();
		m_refString = "";
	}

	public Call(String call, String refString, Operation operation) {
		assert(operation == Operation.ADD);
		m_operation = operation;
		m_calls = new ArrayList<String>();
		m_calls.add(call);
		m_refString = refString;		
	}

	public int numArgs() {
    	switch (m_operation) {
    	case ADD:
            return 2;         
    	case REMOVE:
            return m_calls.size();
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
    		return "call"; 
    	case LIST:
    		return "calls";
    	}    			
    	assert(false);
        return "";
	}

	public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    		assert(m_calls.size() == 1);
    		return "call " + m_refString + " " + m_calls.get(0); 
    	case REMOVE:
    		String commandLine = "call";
    		for (String call : m_calls) {
    			commandLine += " " + call;
    		}
    		return commandLine;
    	case LIST:
    		return "calls";
    	}    			
    	assert(false);
        return "";
	}

	public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "call <CALL_REF> <label>: Adds the given call to the calls list";         
    	case REMOVE:
            return "call [label]+: Removes the given calls from the calls list (supports regular expressions)";
    	case LIST:
            return "calls: Lists the calls";
    	}    		
    	assert(false);
        return "";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
    	switch (m_operation) {
    	case ADD:
    		return new Call(args[index + 2], args[index + 1], m_operation);            
    	case REMOVE:
    		Call call = new Call(m_operation);
    		int i = 1;
    		while (!areArgsDone(args, index + i)) {
    			call.m_calls.add(args[index + i]);
    			i++;
    		}

    		if (call.m_calls.size() < 1)
    			return null;

    		return call;
    	case LIST:
    		return new Call(m_operation);            
    	}    						
    	assert(false);
        return null;
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

    	switch (m_operation) {
    	case ADD:
    		assert(m_calls.size() == 1);
    		Reference ref = ReferenceFactory.createReference(
    				m_refString, ReferenceClass.CALL_REF);    		
			engine.addCall(m_calls.get(0), ref);
			break;
    	case REMOVE:
    		for (String endpoint : m_calls) {
    			engine.removeCall(endpoint);
    		}
			break;			
    	case LIST:
        	listCalls(engine, console);        	
    		break;    		
        default:
           	assert(false);
       		break;
    	}
	}
	
	private void listCalls(Engine engine, Console console) {
		if (engine.getCalls().entrySet().size() == 0){
			console.display("none\n");
			return;
		}

		for (CodePointInfo info : engine.getCalls().values()) {
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
