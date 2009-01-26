package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class SrcPaths extends Command {
	private String m_paths;

	SrcPaths() {
		m_paths = "";
	}
	
	SrcPaths(String paths) {
		m_paths = paths;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "srcpaths";
    }        

    public String commandLine() {
   		return "srcpaths " + m_paths;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "srcpaths <paths>: Sets the (semicolon separated) source search path";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {    	    	
		if ((index + numArgs()) >= args.length) 
			return null;
		
		return new SrcPaths(args[index + 1]);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	engine.setSrcPaths(m_paths);
    }    
}
