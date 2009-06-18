package com.xmos.xta.console.commands;

import java.io.File;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

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
	
    private static boolean m_found;
    
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
    
    public static boolean validateNodeId(Engine engine, Console console, final int routeId, final int nodeId) {
    	if (engine.getRoutes().size() == 0) {
    		console.display("Error: No routes analysed\n");
    		return false;
    	}
    	
    	Route route = engine.getRoute(routeId);
    	if (route == null) {
    		console.display(String.format("Error: invalid route id: %d\n", routeId));
    		return false;
    	}
    	
    	m_found = false;
    	for (Node node : route.getSubRoutes()) {
    	    node.traverse(0, new UntypedNodeTraverser() {
    	    	public boolean foundNode(Node node, int depth) {
    	    		if (node.getId() == nodeId) {
    	    			m_found = true;
    	    		}
    	        	return true;
    	    	}
    	    });    		
    	}
    	return m_found;
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
    
    protected boolean areArgsDone(String[] args, int i) {
		if ((i >= args.length) || 
			(args[i].startsWith("-") && (args[i].length() > 1)) || 
			args[i].startsWith("\n")) {
			return true;
		}
		return false;
	}
}
