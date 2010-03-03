package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class Functions extends Command {
	public Functions() {
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

    public Command clone(String[] args, int index) {
        return new Functions();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (NodeCorePair activeCore : engine.getActiveCores()) {			
	        Collection<String> functionNames = engine.getNonSystemFunctionNames(activeCore.getNode(), activeCore.getCore());
	        if (functionNames.size() > 0) {
	            console.display(String.format("Node: %d, Core: %d\n\n", activeCore.getNode(), activeCore.getCore()));

	        	for (String functionName : functionNames) {
	            	for (long functionAddress : engine.getFunctionPcs(functionName, activeCore.getNode(), activeCore.getCore())){
	            		console.display(String.format("- %s (0x%x)\n", functionName, functionAddress));
	            	}
	            }
	            console.display("\n");
	        }				
		}			
    }
}
