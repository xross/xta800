package com.xmos.xta.console.commands.List;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathQuery;

public class Queries extends Command {
	public Queries() {
	}

    public int numArgs() {
    	return 0;
    }        
	
    public String name() {
    	return "queries";
    }        

    public String commandLine() {
    	return "queries";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "queries: Lists the current queries";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        return new Queries();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		for(PathQuery pq : engine.getPathQueries()){
			console.display(String.format("- %s %s : %d paths\n", pq.getName(), 
					pq.getExpression(), pq.getBlocks().size()));
		}
    }
}
