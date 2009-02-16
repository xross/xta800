package com.xmos.xta.console.commands.Summary;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class Complete extends Command {
	public Complete() {}
		
    public int numArgs()        { return 0; }        
    public String name()        { return "complete"; }        
    public String commandLine() { return "complete"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "complete: Show timing of complete path(s)";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new Complete();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		SummaryDisplayer displayer = new SummaryDisplayer(
				engine, console, SummaryDisplayer.Level.complete);		
		displayer.display();
    }
}
