package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class CompleteBlocks extends Command {
	public CompleteBlocks() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "complete_blocks"; }        
    public String commandLine() { return "complete_blocks"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "complete_blocks: Show timing of complete path(s) + loops + blocks";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new CompleteBlocks();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		SummaryDisplayer displayer = new SummaryDisplayer(
				engine, console, SummaryDisplayer.Level.complete_blocks);		
		displayer.display();
    }
}
