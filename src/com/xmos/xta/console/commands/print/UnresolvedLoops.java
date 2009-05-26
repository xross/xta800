package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class UnresolvedLoops extends Command {
	public UnresolvedLoops() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "unresolved_loops"; }        
    public String commandLine() { return "unresolved_loops"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "unresolved_loops: Show timing of complete paths + loops";
    }

    public Command clone(String[] args, int index) {
   		return new UnresolvedLoops();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		TreeDisplayer displayer = new TreeDisplayer(
				engine, console, TreeDisplayer.Level.unresolved_loops);		
		displayer.display();
    }
}
