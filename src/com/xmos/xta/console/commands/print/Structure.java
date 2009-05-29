package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Structure extends Command {
	public Structure() {
	}
	
    public int numArgs() {
    	return 0;
    }        

    public String name() {
    	return "structure";
    }        

    public String commandLine() {
    	return "structure";
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "structure : Displays the code structure";
    	return descString;    	                
    }

    public Command clone(String[] args, int index) {
    	return new Structure();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
				
		StructureDisplayer displayer = new StructureDisplayer(engine, console);	
		displayer.display();
    }    
}
