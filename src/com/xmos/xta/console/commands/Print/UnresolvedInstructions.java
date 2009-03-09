package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class UnresolvedInstructions extends Command {
	public UnresolvedInstructions() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "unresolved_instructions"; }        
    public String commandLine() { return "unresolved_instructions"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "unresolved_instructions: Show timing of complete paths + loops + blocks + instructions";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new UnresolvedInstructions();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		TreeDisplayer displayer = new TreeDisplayer(
				engine, console, TreeDisplayer.Level.unresolved_instructions);		
		displayer.display();
    }
}
