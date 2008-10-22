package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Function extends Command {
	String m_functionName;
	
	Function() {
		m_functionName = "";
	}
	
	Function(String functionName) {
		m_functionName = functionName;
	}

    public int numArgs() {
    	return 1;
    }        
	
    public String name() {
    	return "function";
    }        

    public String commandLine() {
    	return "function " + m_functionName;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "function <name>: Sets a path to trace a given function";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	String functionName = args[index + 1];
        return new Function(functionName);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        if (!engine.analyseRoute(m_functionName)) {  
         	console.display(String.format("Error: %s\n", engine.getErrorString()));
        }            
    }
}
