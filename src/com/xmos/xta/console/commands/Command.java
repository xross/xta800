package com.xmos.xta.console.commands;

import java.io.File;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;

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
    public abstract Command clone(String[] args, int index);
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
    	if (executable == null) {
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
    
    public static boolean validateRoutes(Engine engine, Console console) {
       	if (engine.getRoutes().size() == 0) {
    		console.display("Error: No routes analysed\n");
    		return false;
     	}
        return true;  	
    }

    public static boolean validateRouteId(Engine engine, Console console, int routeId) {
    	if (engine.getRoutes().size() == 0) {
    		console.display("Error: No routes analysed\n");
    		return false;
    	}
    	
    	Route route = engine.getRoute(routeId);
    	if (route == null) {
    		console.display(String.format("Error: invalid route id: %d\n", routeId));
    		return false;
    	}
    	return true;
    }
    
    public static boolean validateIds(Engine engine, Console console, int routeId, int id, IdType idType) {
    	if (engine.getRoutes().size() == 0) {
    		console.display("Error: No routes analysed\n");
    		return false;
    	}
    	
    	Route route = engine.getRoute(routeId);
    	if (route == null) {
    		console.display(String.format("Error: invalid route id: %d\n", routeId));
    		return false;
    	}
    	
    	switch (idType) {
        case BLOCK:
        	// TODO
        	//if (route.getBlock(id) == null) {
        	//	console.display(String.format("Error: invalid block id: %d\n", id));        		
        	//	return false;
        	//}
        	return true;
        case PATH:
        	// TODO
        	//if (route.getPath(id) == null) {
        	//	console.display(String.format("Error: invalid path id: %d\n", id));        		
        	//	return false;
        	//}    	
        	return true;        	
        default:
        	assert(false);
            return false;
        }    	
    }
    
    public static int getRouteId(String routeId) {
    	if (routeId.equals("-")) {
    		return Engine.ROUTE_ID_LAST;
    	}    	
    	
    	int route = Integer.parseInt(routeId);
    	return route;
    }

    public static String getRouteId(int routeId) {
    	if (routeId == Engine.ROUTE_ID_LAST) {
    		return "-";
    	}
    	return String.format("%d", routeId);
    }
    
    public static int getId(String idString) throws NumberFormatException {    	
    	if (getIdType(idString) == IdType.BLOCK) {
        	return Integer.parseInt(idString.substring(1));
    	}    
    	
    	if (idString.equals("-")) {
    		return Route.WORST_CASE_PATH_ID;
    	}    	
    	return Integer.parseInt(idString);
    }

    public static String getId(int id, Command.IdType idType) {
    	if (idType == IdType.BLOCK) {
    		return String.format("b%d", id);
    	}
    	
    	if (id == Route.WORST_CASE_PATH_ID) {
    		return "-";
    	}
    	return String.format("%d", id);
    }
    
    public static IdType getIdType(String idString) {
		if (idString.charAt(0) == 'b') {
			return IdType.BLOCK;
		}
		return IdType.PATH;
    }

    protected boolean areArgsDone(String[] args, int i) {
		if ((i >= args.length) || 
			(args[i].startsWith("-") && (args[i].length() > 1)) || 
			args[i].startsWith("\n"))
			return true;
		return false;
	}
}
