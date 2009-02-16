package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class CompleteLoops extends Command {
	public CompleteLoops() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "complete_loops"; }        
    public String commandLine() { return "complete_loops"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "complete_loops: Show timing of complete path(s) + loops";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new CompleteLoops();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		SummaryDisplayer displayer = new SummaryDisplayer(
				engine, console, SummaryDisplayer.Level.complete_loops);		
		displayer.display();
    }
}
