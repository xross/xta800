package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class Unresolved extends Command {
	public Unresolved() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "unresolved"; }        
    public String commandLine() { return "unresolved"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "unresolved: Show timing of unresolved path(s)";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new Unresolved();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		SummaryDisplayer displayer = new SummaryDisplayer(
				engine, console, SummaryDisplayer.Level.unresolved);		
		displayer.display();
    }
}
