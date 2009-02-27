package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class CompleteInstructions extends Command {
	public CompleteInstructions() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "complete_instructions"; }        
    public String commandLine() { return "complete_instructions"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "complete_instructions: Show timing of complete path(s) + loops + blocks + instructions";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new CompleteInstructions();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		SummaryDisplayer displayer = new SummaryDisplayer(
				engine, console, SummaryDisplayer.Level.complete_instructions);		
		displayer.display();
    }
}
