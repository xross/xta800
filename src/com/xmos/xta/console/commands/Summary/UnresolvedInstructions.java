package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class UnresolvedInstructions extends Command {
	public UnresolvedInstructions() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "unresolved_instructions"; }        
    public String commandLine() { return "unresolved_instructions"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "unresolved_instructions: Show timing of complete path(s) + loops + blocks + instructions";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new UnresolvedInstructions();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		SummaryDisplayer displayer = new SummaryDisplayer(
				engine, console, SummaryDisplayer.Level.unresolved_instructions);		
		displayer.display();
    }
}
