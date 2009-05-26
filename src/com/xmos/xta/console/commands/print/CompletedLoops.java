package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class CompletedLoops extends Command {
	public CompletedLoops() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "completed_loops"; }        
    public String commandLine() { return "completed_loops"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "completed_loops: Show timing of complete paths + loops";
    }

    public Command clone(String[] args, int index) {
   		return new CompletedLoops();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		TreeDisplayer displayer = new TreeDisplayer(
				engine, console, TreeDisplayer.Level.complete_loops);		
		displayer.display();
    }
}
