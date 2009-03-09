package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class CompletedPaths extends Command {
	public CompletedPaths() {}
		
    public int numArgs()        { return 0; }        
    public String name()        { return "completed_paths"; }        
    public String commandLine() { return "completed_paths"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "completed_paths: Show timing of complete paths";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new CompletedPaths();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		TreeDisplayer displayer = new TreeDisplayer(
				engine, console, TreeDisplayer.Level.complete_paths);		
		displayer.display();
    }
}
