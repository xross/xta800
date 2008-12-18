package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Mode extends Command {
	private boolean m_resolve;

	Mode() {
		m_resolve = false;
	}
	
	Mode(boolean mode) {
		m_resolve = mode;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "mode";
    }        

    public String commandLine() {
    	if (m_resolve){
    		return "mode resolve";
    	} else {
    		return "mode noresolve";
    	}
    }
    
    public String description(Engine engine, boolean detailed) {
        return "mode <resolve|noresolve>: Sets the engine the given mode";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {    	    	
		if ((index + numArgs()) >= args.length) 
			return null;
		
		if (args[index +1].equals("resolve")){
			return new Mode(true);
		} else if(args[index +1].equals("noresolve")){
			return new Mode(false);
		} 
		return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        engine.setResolve(m_resolve);                
   }    
}
