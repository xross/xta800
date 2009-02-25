package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;

public class Simple extends Command {
	public Simple() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "simple"; }        
    public String commandLine() { return "simple"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "simple: Show simple paths summary";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new Simple();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;		    	

		displaySimple(console, engine.getRoute());
    }

    private void displaySimple(Console console, Route route) {
    	console.display(String.format("Complete paths:   %d\n", route.getCompletedPaths().size()));
    	console.display(String.format("Unresolved paths: %d\n", route.getUnresolvedPaths().size()));
    }
	
}
