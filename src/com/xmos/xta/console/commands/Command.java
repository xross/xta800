package com.xmos.xta.console.commands;

import java.io.File;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;

public abstract class Command {
	public String toString(){
		return commandLine();
	}
    public abstract int numArgs();
    public abstract String name();
    public abstract String commandLine();
    public abstract String description(Engine engine, boolean detailed);
    public abstract Command clone(String[] args, int index, boolean commandLineMode);
    public abstract void execute(Shell shell, Engine engine, Console console);
    
    public static boolean isExecutableLoaded(Engine engine, Console console) {    
	    File executable = engine.getExecutable();
    	if (executable==null) {
    		console.display("Error: No executable loaded\n");
	        return false;
    	}
    	return true;
    }

    public static boolean isPresent(Engine engine, int nodeId, int coreId, Console console) { 
    	if (engine.getXeLoader().getMemoryImage(nodeId, coreId) == null) {
    		console.display(String.format("Error: No code loaded on node: %d, core: %d\n", nodeId, coreId));
    		return false;
    	}
    	return true;    	
    }

    public static boolean isRouteAnalysed(Engine engine, Console console) { 
    	if (engine.getRoute() == null) {
    		console.display("Error: No function/path analysed\n");
    		return false;
    	}
    	return true;    	
    }

    public static boolean isPathValid(Engine engine, Console console, int pathId) {
    	Block path = Block.getPath(engine.getRoute(), pathId);
    	if (path == null) {
    		console.display(String.format("Error: invalid path id: %d\n", pathId));        		
    		return false;
    	}    	
    	return true;
    }    
}
