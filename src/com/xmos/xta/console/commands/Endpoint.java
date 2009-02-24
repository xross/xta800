package com.xmos.xta.console.commands;

import java.util.ArrayList;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.SrcLabelInfo;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;

public class Endpoint extends Command {
	public enum Operation {
		ADD, REMOVE, LIST
	};

	private ArrayList<String> m_endpoints;
	private String m_refString;
	private Operation m_operation;

	public Endpoint(Operation operation) {
		m_operation = operation;
		m_endpoints = new ArrayList<String>();
		m_refString = "";
	}

	public Endpoint(String endpoint, String refString, Operation operation) {
		assert(operation == Operation.ADD);
		m_operation = operation;
		m_endpoints = new ArrayList<String>();
		m_endpoints.add(endpoint);
		m_refString = refString;		
	}

	public int numArgs() {
    	switch (m_operation) {
    	case ADD:
            return 2;         
    	case REMOVE:
            return m_endpoints.size();
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
    		return "endpoint"; 
    	case LIST:
    		return "endpoints";
    	}    			
    	assert(false);
        return "";
	}

	public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    		assert(m_endpoints.size() == 1);
    		return "endpoint " + m_refString + " " + m_endpoints.get(0); 
    	case REMOVE:
    		String commandLine = "endpoint";
    		for (String endpoint : m_endpoints) {
    			commandLine += " " + endpoint;
    		}
    		return commandLine;
    	case LIST:
    		return "endpoints";
    	}    			
    	assert(false);
        return "";
	}

	public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "endpoint <ref> <label>: Adds the given endpoint to the endpoints list";         
    	case REMOVE:
            return "endpoint [label]+: Removes the given endpoints from the endpoints list (supports regular expressions)";
    	case LIST:
            return "endpoints: Lists the endpoints";
    	}    		
    	assert(false);
        return "";
	}

	public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length)
			return null;
				
    	switch (m_operation) {
    	case ADD:
    		return new Endpoint(args[index + 2], args[index + 1], m_operation);            
    	case REMOVE:
    		Endpoint endpoint = new Endpoint(m_operation);
    		int i = 1;
    		while (!areArgsDone(args, index + i)) {
    			endpoint.m_endpoints.add(args[index + i]);
    			i++;
    		}

    		if (endpoint.m_endpoints.size() < 1)
    			return null;

    		return endpoint;
    	case LIST:
    		return new Endpoint(m_operation);            
    	}    						
    	assert(false);
        return null;
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

    	switch (m_operation) {
    	case ADD:
    		assert(m_endpoints.size() == 1);
    		CodeReference ref = CodeReferenceFactory.createEndpointReference(m_refString);
			engine.addEndpoint(m_endpoints.get(0), ref);
			break;
    	case REMOVE:
    		for (String endpoint : m_endpoints) {
    			engine.removeEndpoint(endpoint);
    		}
			break;			
    	case LIST:
        	listEndpoints(engine, console);        	
    		break;    		
        default:
           	assert(false);
       		break;
    	}
	}
	
	private void listEndpoints(Engine engine, Console console) {
		if (engine.getEndpoints().entrySet().size() == 0){
			console.display("none\n");
			return;
		}

		for (SrcLabelInfo info : engine.getEndpoints().values()) {
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
