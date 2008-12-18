package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.CodeReference;
import com.xmos.xta.engine.Engine.ReferenceMode;

public class Prohibited extends Command {	
	enum Operation {
		ADD, REMOVE, LIST
	};
	
	private CodeReference m_ref;	
	private Operation m_operation;
	
	Prohibited(Operation operation) {
		m_operation = operation;
        m_ref = null;
    }
    
	Prohibited(CodeReference ref, Operation operation) {
        m_ref = ref;        
        m_operation = operation;
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
        return "prohibited";
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return "prohibited " + m_ref.getRef();
    	case LIST:
    		return "prohibited";
        default:
        	assert(false);
            return "";
    	}    	        
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "prohibited <ref>: Adds the given reference to the prohibited list (pre analysis action)";         
    	case REMOVE:
            return "prohibited <ref>: Removes the given reference from the prohibited list (pre analysis action)";
    	case LIST:
            return "prohibited: Lists the reference in the excluded list";
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
            return new Prohibited(codeRef, m_operation);
    	case LIST:
            return new Prohibited(m_operation);
        default:
        	assert(false);
            return null;
    	}    	        
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;    
        
        if (m_operation == Operation.LIST) {
        	listProhibiteds(engine, console);        	
        } else {
            m_ref.resolve(engine);
            if (m_ref.isResolved()){
            	switch (m_operation) {
            	case ADD:
            		engine.addReference(ReferenceMode.PROHIBITED, m_ref);
            		break;
            	case REMOVE:
            		engine.removeReference(ReferenceMode.PROHIBITED, m_ref);
            		break;
                default:
                	assert(false);
            		break;
            	}    	
            } else {
            	console.display("Error: Unresolved reference: " + m_ref + "\n");
            }               	
        }      
    }  

	private void listProhibiteds(Engine engine, Console console) {
		for(CodeReference ref : engine.getReference(ReferenceMode.PROHIBITED)){
    		console.display(String.format("- ref: %s, pc: 0x%x\n", ref.getRef(), ref.getPc()));
    	}
	}
}
