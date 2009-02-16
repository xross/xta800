package com.xmos.xta.console.commands;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Engine.ReferenceMode;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;

public class Required extends Command {	
	public enum Operation {
		ADD, REMOVE, LIST
	};
	
	private String m_refString;	
	private Operation m_operation;
	
	public Required(Operation operation) {
        m_operation = operation;
        m_refString = "";
    }
    
	public Required(String refString, Operation operation) {
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
        return "required";
    }        

    public String commandLine() {
    	switch (m_operation) {
    	case ADD:
    	case REMOVE:
    		return "required " + m_refString;
    	case LIST:
    		return "required";
        default:
        	assert(false);
            return "";
    	}    	        
    }
    
    public String description(Engine engine, boolean detailed) {
    	switch (m_operation) {
    	case ADD:
            return "required <ref>: Adds the given reference to the required list (post analysis action)";         
    	case REMOVE:
            return "required <ref>: Removes the given reference from the required list (post analysis action)";
    	case LIST:
            return "required: Lists the reference in the excluded list";
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
            return new Required(args[index + 1], m_operation);
    	case LIST:
            return new Required(m_operation);
        default:
        	assert(false);
            return null;
    	}    	        
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;    

        CodeReference ref = CodeReferenceFactory.createCodeReference(m_refString);

        switch (m_operation) {
       	case ADD:
       		engine.addReference(ReferenceMode.REQUIRED, ref);
       		break;
       	case REMOVE:
       		engine.removeReference(ReferenceMode.REQUIRED, ref);        			
       		break;           		
       	case LIST:
       		listRequirements(engine, console);
       		break;
        default:
           	assert(false);
       		break;
        }                     
    }  

	private void listRequirements(Engine engine, Console console) {
		if(engine.getReference(ReferenceMode.REQUIRED).size() == 0){
			console.display("none\n");
			return;
		}
    	for(CodeReference ref : engine.getReference(ReferenceMode.REQUIRED)){
    		console.display(String.format("- ref: %s, pc: 0x%x\n", ref.getRef(), ref.getPc()));
    	}
	}    
}
