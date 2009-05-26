package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class CompleteBlocks extends Command {
	public CompleteBlocks() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "completed_blocks"; }        
    public String commandLine() { return "completed_blocks"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "completed_blocks: Show timing of complete paths + loops + blocks";
    }

    public Command clone(String[] args, int index) {
   		return new CompleteBlocks();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		TreeDisplayer displayer = new TreeDisplayer(
				engine, console, TreeDisplayer.Level.complete_blocks);		
		displayer.display();
    }
}
