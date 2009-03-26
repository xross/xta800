package com.xmos.xta.console.commands.Analyse;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Function extends Command {
	private String m_functionName;
	
	public Function() {
		m_functionName = "";
	}
	
	public Function(String functionName) {
		m_functionName = functionName;
	}

    public int numArgs() {
    	return 1;
    }        
	
	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.FUNCTION};
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

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	String functionName = args[index + 1];
        return new Function(functionName);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        engine.analyseRoute(m_functionName);  
    }
}
