package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Engine.ExpressionMode;

class Mode extends Command {
	boolean m_resolve;

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
    		return "mode RESOLVE";
    	} else {
    		return "mode NORESOLVE";
    	}
    }
    
    public String description(Engine engine, boolean detailed) {
        return "mode <mode type> : sets the engine into RESOLVE or NORESOLVE mode";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	
    	
		if ((index + numArgs()) >= args.length) 
			return null;		
		if(args[index +1].equals("RESOLVE")){
			return new Mode(true);
		} else if(args[index +1].equals("NORESOLVE")){
			return new Mode(false);
		} 
		return new Mode(m_resolve);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
        engine.setResolve(m_resolve);                
   }    
}
