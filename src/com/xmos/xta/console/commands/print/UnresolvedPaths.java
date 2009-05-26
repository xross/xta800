package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class UnresolvedPaths extends Command {
	public UnresolvedPaths() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "unresolved_paths"; }        
    public String commandLine() { return "unresolved_paths"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "unresolved_paths: Show timing of unresolved path(s)";
    }

    public Command clone(String[] args, int index) {
   		return new UnresolvedPaths();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		TreeDisplayer displayer = new TreeDisplayer(
				engine, console, TreeDisplayer.Level.unresolved_paths);		
		displayer.display();
    }
}
