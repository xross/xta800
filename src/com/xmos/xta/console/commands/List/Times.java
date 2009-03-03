package com.xmos.xta.console.commands.List;

import java.util.ArrayList;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Times extends Command {
	public Times() {
	}
	
    public int numArgs() {
    	return 0;
    }

    public String name() {
    	return "times";
    }        

    public String commandLine() {
    	return "times";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "times: Lists the instructions which have had their times set";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		return new Times();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	ArrayList<Integer> pcs = engine.getPcsWithTimeSet();

    	if (pcs == null)
    		return;
    	else if (pcs.size() == 0) {
			console.display("none\n");
			return;
		}

    	for (Integer pc : pcs) {
    	    console.display(String.format("- pc: 0x%x, %.1fns, %d cycles\n", pc, 
    	    		engine.getInstructionTimeTaken(pc), engine.getInstructionNumCycles(pc)));
    	}		
    }    
}
