package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class CompletedInstructions extends Command {
	public CompletedInstructions() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "completed_instructions"; }        
    public String commandLine() { return "completed_instructions"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "completed_instructions: Show timing of complete paths + loops + blocks + instructions";
    }

    public Command clone(String[] args, int index) {
   		return new CompletedInstructions();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		TreeDisplayer displayer = new TreeDisplayer(
				engine, console, TreeDisplayer.Level.complete_instructions);		
		displayer.display();
    }
}
