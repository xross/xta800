package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Engine.ReferenceMode;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;

public class Filter extends Command {	
	public enum Operation {
		ADD, REMOVE, LIST
	};
	
	private String m_refString;	
	private Operation m_operation;
	
	public Filter(Operation operation) {
		m_operation = operation;
		m_refString = "";
    }
    
	public Filter(String refString, Operation operation) {
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
            return "filter";
    	case LIST:
            return "filters";
        default:
        	assert(false);
            return "";
    	}    	        
    }        

    public String commandLine() {    	        
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return "filter " + m_refString;
    	case LIST:
    		return "filters";
        default:
        	assert(false);
            return "";
    	}    	        
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "filter <ref>: Adds the given reference to the filter list (post analysis action)";         
    	case REMOVE:
            return "filter <ref>: Removes the given reference from the filter list (supports regular expressions)";
    	case LIST:
            return "filters: Lists the reference in the filter list";
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
    	case REMOVE:
            return new Filter(args[index + 1], m_operation);
    	case LIST:
            return new Filter(m_operation);
        default:
        	assert(false);
            return null;
    	}    	
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;    

        CodeReference ref = CodeReferenceFactory.createSrcLabelReference(m_refString);

       	switch (m_operation) {
       	case ADD:
       		engine.addReference(ReferenceMode.FILTERED, ref);
       		break;
       	case REMOVE:
       		engine.removeReference(ReferenceMode.FILTERED, ref);        			
       		break;           		
       	case LIST:
       		listExcludes(engine, console);
       		break;
        default:
           	assert(false);
       		break;
        }                     
    }  
    
    private void listExcludes(Engine engine, Console console) {    
    	if (engine.getReference(ReferenceMode.FILTERED).size() == 0){
			console.display("none\n");
			return;
		}
    	for (CodeReference ref : engine.getReference(ReferenceMode.FILTERED)){
    		console.display(String.format("- ref: %s, pc: 0x%x\n", ref.getRef(), ref.getPc()));
    	}
    }
}
