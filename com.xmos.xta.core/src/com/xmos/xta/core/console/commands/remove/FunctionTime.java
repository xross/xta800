package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class FunctionTime extends Command {
	private String m_functionName;

	public FunctionTime() {
	}
	
	public FunctionTime(String functionName) {
		m_functionName = functionName;
	}

    public int numArgs() {
		return 1;
    }        
	
	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.FUNCTION};
	}    
    
    public String name() {
		return "functiontime"; 
    }        

    public String commandLine() {
       	return "functiontime " + m_functionName;        	
    }
    
    public String description(Engine engine, boolean detailed) {
    	return "functiontime <FUNCTION_REF|*>: Removes the given functon time from the list of defines";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) { 
			return null;
		}

    	return new FunctionTime(args[index + 1]);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		engine.removeFunctionTime(getSrc(), m_functionName);
    }
}
