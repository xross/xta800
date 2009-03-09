package com.xmos.xta.console.commands.List;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class Unknowns extends Command {
	public Unknowns() {
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

		boolean found = false;
		for (Route route : engine.getRoutes()) {
			java.util.List<XtaInstruction> instructions = route.getUnknowns();		
			if (instructions.size() > 0) {
				for (XtaInstruction instruction : instructions) {					
				    console.display(String.format("Time taken for instruction (0x%x: %s %s) is unknown\n",
					    instruction.getPc(), instruction.getNmenonic(), instruction.getOperandString()));
				    found = true;
				}			
			}			
		}
		if (!found) {
		    console.display("none\n");
		}
    }
}
