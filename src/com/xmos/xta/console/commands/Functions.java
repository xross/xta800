package com.xmos.xta.console.commands;

import java.util.Vector;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Functions extends Command {
	Functions() {
	}

    public int numArgs() {
    	return 0;
    }        
	
    public String name() {
    	return "functions";
    }        

    public String commandLine() {
    	return "functions";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "functions: Lists the functions in the loaded application";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        return new Functions();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			
        
	    int nodeId = engine.getNodeId();
	    int coreId = engine.getCoreId();
        Vector<String> functionNames = engine.getXeLoader().getFunctionNames(nodeId, coreId);

        for (String functionName : functionNames) {
            console.display(String.format("- %s\n", functionName));
        }
    }
}
