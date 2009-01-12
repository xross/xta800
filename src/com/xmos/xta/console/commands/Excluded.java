package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.CodeReference;
import com.xmos.xta.engine.Engine.ReferenceMode;

public class Excluded extends Command {	
	enum Operation {
		ADD, REMOVE, LIST
	};
	
	private CodeReference m_ref;	
	private Operation m_operation;
	
	Excluded(Operation operation) {
		m_operation = operation;
        m_ref = null;
    }
    
	Excluded(CodeReference ref, Operation operation) {
		m_operation = operation;
        m_ref = ref;        
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
        return "excluded";
    }        

    public String commandLine() {    	        
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return "excluded " + m_ref.getRef();
    	case LIST:
    		return "excluded";
        default:
        	assert(false);
            return "";
    	}    	        
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "excluded <ref>: Adds the given reference to the excluded list (post analysis action)";         
    	case REMOVE:
            return "excluded <ref>: Removes the given reference from the excluded list (post analysis action)";
    	case LIST:
            return "excluded: Lists the reference in the excluded list";
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
            CodeReference codeRef = new CodeReference(args[index + 1]);
            return new Excluded(codeRef, m_operation);
    	case LIST:
            return new Excluded(m_operation);
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
       		if (!engine.addReference(ReferenceMode.EXCLUSION, m_ref)) {
               	console.display("Error: Unable to add reference: " + m_ref.getRef() + "\n");            			
       		}
       		break;
       	case REMOVE:
       		if (!engine.removeReference(ReferenceMode.EXCLUSION, m_ref)) {        			
             	console.display("Error: Unable to remove reference: " + m_ref.getRef() + "\n");            			
      		}
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
    	for (CodeReference ref : engine.getReference(ReferenceMode.EXCLUSION)){
    		console.display(String.format("- ref: %s, pc: 0x%x\n", ref.getRef(), ref.getPc()));
    	}
    }
}
