package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.CodeReference;
import com.xmos.xta.engine.Engine.ReferenceMode;

public class Prohibited extends Command {
	
	enum AddRemove {
		ADD, REMOVE
	};
	
	CodeReference m_ref;
	
	AddRemove m_addRemove;
	
	Prohibited() {
        m_ref = null;
    }
    
	Prohibited(CodeReference ref, AddRemove addRemove) {
        m_ref = ref;        
        m_addRemove = addRemove;
    }
    
    public int numArgs() {
        return 2;
    }        

    public String name() {
        return "prohibited";
    }        

    public String commandLine() {
        return "prohibited " + m_addRemove + " " + m_ref.getRef();
    }
    
    public String description(Engine engine, boolean detailed) {
        return "prohibited <add/remove> <ref>: Adds or removes the given reference to the prohibited list (during analysis action)";         
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        if ((index + numArgs()) >= args.length) 
            return null;        
        AddRemove addRemove; 
        String arg = args[index + 1];
        if(arg.toUpperCase().equals("ADD") ) {
        	addRemove = AddRemove.ADD;
        } else if(arg.toUpperCase().equals("REMOVE") ) {
        	addRemove = AddRemove.REMOVE;
        } else {
        	return null;
        }
       
        CodeReference codeRef = new CodeReference(args[index + 2]);
        return new Prohibited(codeRef, addRemove);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;    
        m_ref.resolve(engine);
        if(m_ref.isResolved()){
	        if(m_addRemove == AddRemove.ADD){
	        	engine.addReference(ReferenceMode.PROHIBITED, m_ref);
	        } else if(m_addRemove == AddRemove.REMOVE){
	        	engine.removeReference(ReferenceMode.PROHIBITED, m_ref);
	        } else {
	        	assert(false);
	    	}
        } else {
        	console.display("Error: trying to add an unresolved reference: " + m_ref + "\n");
        }
       
    }  

}
