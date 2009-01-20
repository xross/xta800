package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.SummaryDisplayer;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Unknown;

class Unknowns extends Command {
	
	Unknowns() {
	}
	
    public int numArgs() {
    	return 0;
    }        

    public String name() {
    	return "unknowns";
    }        

    public String commandLine() {
    	return "unknowns";
    }        
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("unknowns : Displays a list of the unknowns within the current analysis");
    	return descString;
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	return new Unknowns();
   		    
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;
		for(Unknown unknown : engine.getRoute().getUnknowns()){
			console.display(unknown.getReason() + "\n");
		}	
    }
}
