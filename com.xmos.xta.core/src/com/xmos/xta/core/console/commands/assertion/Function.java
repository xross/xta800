package com.xmos.xta.core.console.commands.assertion;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;

public class Function extends Command {
	private String m_functionName;
	private TimeUnit m_timeUnit;
	
	public Function() {
		m_functionName = "";
	}
	
	public Function(String functionName) {
		m_functionName = functionName;
		m_timeUnit = null;
	}
	
	public Function(String functionName, TimeUnit timeUnit) {
		m_functionName = functionName;
		m_timeUnit = timeUnit;
	}
	
    public int numArgs() {
    	return 3;
    }        
	
	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.FUNCTION};
	}    
    
    public String name() {
    	return "function";
    }        

    public String commandLine() {
       	return String.format("function %s %s", m_functionName, 
       			m_timeUnit.getDescription(false));
    }
    
    public String description(Engine engine, boolean detailed) {
        String descString = "function <name> <value> <mode>: Assert timing requirement for the given function";         
       	if (detailed) {    	    	
       		descString += "\n" + TimeUnit.getHelp();
       	}
        return descString;
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

        engine.analyseRoute(getSrc(), m_functionName);  
    }
}
