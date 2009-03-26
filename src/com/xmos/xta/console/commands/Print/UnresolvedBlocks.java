package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class UnresolvedBlocks extends Command {
	public UnresolvedBlocks() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "unresolved_blocks"; }        
    public String commandLine() { return "unresolved_blocks"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "unresolved_blocks: Show timing of complete paths + loops + blocks";
    }

    public Command clone(String[] args, int index) {
   		return new UnresolvedBlocks();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		TreeDisplayer displayer = new TreeDisplayer(
				engine, console, TreeDisplayer.Level.unresolved_blocks);		
		displayer.display();
    }
}
