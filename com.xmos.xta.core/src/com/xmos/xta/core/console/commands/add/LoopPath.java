package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class LoopPath extends Command {
	private String m_refString;
	private int m_iterations;

	public LoopPath() {
	}

	public LoopPath(String refString, int iterations) {
		m_refString = refString;
		m_iterations = iterations;
	}

    public int numArgs() {
   		return 2; 
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}    
    
    public String name() {
		return "looppath"; 
    }        
    
    public String commandLine() {
       	return "looppath " + " " + m_refString + " " + m_iterations;
    }
    
    public String description(Engine engine, boolean detailed) {
    	return "looppath <ANY_REF> <iterations>: Adds the given loop path count define to the list of defines";
    }
    
    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) { 
			return null;
		}

		try {
	   	    int iterations = Integer.parseInt(args[index + 2]);
	        return new LoopPath(args[index + 1], iterations);    		
		} catch (NumberFormatException e) {
	   	    return null;
	   	}    		   	    		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		engine.addLoopPath(getSrc(), m_refString, m_iterations);
    }    
}
