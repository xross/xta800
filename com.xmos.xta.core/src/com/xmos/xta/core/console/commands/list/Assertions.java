package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.assertions.FunctionInfo;
import com.xmos.xta.core.engine.assertions.LoopInfo;
import com.xmos.xta.core.engine.assertions.LoopPathInfo;
import com.xmos.xta.core.engine.assertions.LoopScopeInfo;
import com.xmos.xta.core.engine.assertions.PathInfo;
import com.xmos.xta.core.engine.assertions.TimeInfo;

public class Assertions extends Command {
	public Assertions() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
  		return "assertions";
	}

	public String commandLine() {
   		return "assertions";
	}

	public String description(Engine engine, boolean detailed) {
        return "assertions: Lists the assertions";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
   		return new Assertions();            
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

       	listAssertions(engine, console);        	
	}

	private void listAssertions(Engine engine, Console console) {
		boolean foundAssertion = false;

		for (FunctionInfo info : engine.getFunctionAssertions()) {			
    	    console.display(String.format("Function: %s, time: %s\n", 
    	    		info.getFunction(), info.getTimeUnit().getDescription(false))); 
    	    foundAssertion = true;
		}
		for (PathInfo info : engine.getPathAssertions()) {			
    	    console.display(String.format("Path: %s to %s, time: %s\n", 
    	    		info.getFrom(), info.getTo(), info.getTimeUnit().getDescription(false))); 
    	    foundAssertion = true;
		}
    	for (LoopInfo info : engine.getLoopAssertions()) {
    	    console.display(String.format("Loop: %s, iterations: %d\n", 
    	    		info.getReference().toString(), info.getIterations())); 
    	    foundAssertion = true;
    	}
	    for (LoopPathInfo info : engine.getLoopPathAssertions()) {
    	    console.display(String.format("LoopPath: %s, iterations: %d\n", 
    	    		info.getReference().toString(), info.getIterations())); 
    	    foundAssertion = true;
	    }
  	    for (LoopScopeInfo info : engine.getLoopScopeAssertions()) {
    	    console.display(String.format("LoopScope: %s, scope: %s\n", 
    	    		info.getReference().toString(), info.getAbsolute() ? "a" : "r")); 
    	    foundAssertion = true;
	    }
	    for (TimeInfo info : engine.getTimeAssertions()) {
    	    console.display(String.format("Time: %s, time: %s\n",
    	    		info.getReference().toString(), info.getTimeUnit().getDescription(false))); 
    	    foundAssertion = true;
    	}
	    
	    if (!foundAssertion) {
	    	console.display("none\n");
	    }
	}
}
