package com.xmos.xta.console.commands;

import java.io.File;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public abstract class Command {
    public enum IdType {
    	PATH,
    	BLOCK,
    }
    
    public enum ArgType {
    	FILE,
    	FUNCTION,
    	ENDPOINT,
    	SRCLABEL,
    	HELP,
    }
	
    public abstract int numArgs();
    public abstract String name();
    public abstract String commandLine();
    public abstract String description(Engine engine, boolean detailed);
    public abstract Command clone(String[] args, int index, boolean commandLineMode);
    public abstract void execute(Shell shell, Engine engine, Console console);

	public String toString(){
		return commandLine();
	}
    
	public Command[] getSubCommands() {
		return new Command[] {};
	}
    
	public ArgType[] getArgTypes() {
		return new ArgType[] {};
	}
	
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

    public static boolean isIdValid(Engine engine, Console console, int id, IdType idType) {
        switch (idType) {
        case BLOCK:
        	if (engine.getRoute().getBlock(id) == null) {
        		console.display(String.format("Error: invalid block id: %d\n", id));        		
        		return false;
        	}
        	return true;
        case PATH:
        	if (engine.getRoute().getPath(id) == null) {
        		console.display(String.format("Error: invalid path id: %d\n", id));        		
        		return false;
        	}    	
        	return true;        	
        default:
        	assert(false);
            return false;
        }    	
    }
            
    public static int getId(String idString) {    	
    	if (getIdType(idString) == IdType.BLOCK) {
    		idString = idString.substring(1);
    	}    	
   		return Integer.parseInt(idString);
    }

    public static IdType getIdType(String idString) {
		if (idString.charAt(0) == 'b') {
			return IdType.BLOCK;
		}
		return IdType.PATH;
    }
    
    protected boolean areArgsDone(String[] args, int i) {
		if ((i >= args.length) || args[i].startsWith("-") || args[i].startsWith("\n"))
			return true;
		return false;
	}
}
