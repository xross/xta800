package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class UnresolvedLoops extends Command {
	public UnresolvedLoops() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "unresolved_loops"; }        
    public String commandLine() { return "unresolved_loops"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "unresolved_loops: Show timing of complete path(s) + loops";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new UnresolvedLoops();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		SummaryDisplayer displayer = new SummaryDisplayer(
				engine, console, SummaryDisplayer.Level.unresolved_loops);		
		displayer.display();
    }
}
