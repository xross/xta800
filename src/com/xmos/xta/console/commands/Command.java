package com.xmos.xta.console.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

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
    	if (routeId != Engine.ROUTE_ID_GLOBAL) {
    	    if (engine.getRoutes().size() == 0) {
    		    console.display("Error: No routes analysed\n");
    		    return false;
    	    }
        	Route route = engine.getRoute(routeId);
        	if (route == null) {
        		console.display(String.format("Error: invalid route id: %d\n", routeId));
        		return false;
        	}
    	}
    	return true;
    }
    
    public static boolean validateNodeId(Engine engine, Console console, int routeId, int nodeId) {
    	if (routeId == Engine.ROUTE_ID_GLOBAL) {
        	Node node = engine.getStructureGraph().getNode(nodeId);
        	if (node == null) {
        		console.display(String.format("Error: invalid node id: %d\n", nodeId));
        		return false;    		
        	}        	    		    		
    	} else {
	    	if (engine.getRoutes().size() == 0) {
	    		console.display("Error: No routes analysed\n");
	    		return false;
	    	}    	
        	Route route = engine.getRoute(routeId);
        	if (route == null) {
        		console.display(String.format("Error: invalid route id: %d\n", routeId));
        		return false;
        	}   
        	Node node = route.getNode(nodeId);
        	if (node == null) {
        		console.display(String.format("Error: invalid node id: %d\n", nodeId));
        		return false;    		
        	}        	
    	}    	    	    	
    	return true;
    }
    
    public static int getRouteId(String routeId) {
    	if (routeId.equals("-")) {
    		return Engine.ROUTE_ID_LAST;
    	} else if (routeId.equals("#")) {
    		return Engine.ROUTE_ID_GLOBAL;
    	}
    	int route = Integer.parseInt(routeId);
    	return route;
    }

    public static String getRouteId(int routeId) {
    	if (routeId == Engine.ROUTE_ID_LAST) {
    		return "-";
    	} else if (routeId == Engine.ROUTE_ID_GLOBAL) {
    		return "#";
    	}
    	return String.format("%d", routeId);
    }

    public static int getNodeId(String nodeId) {
    	if (nodeId.equals("-")) {
    		return Engine.NODE_ID_TOP_LEVEL;
    	} 
    	int node = Integer.parseInt(nodeId);
    	return node;
    }

    public static String getNodeId(int nodeId) {
    	if (nodeId == Engine.NODE_ID_TOP_LEVEL) {
    		return "-";
    	} 
    	return String.format("%d", nodeId);
    }    

    protected boolean areArgsDone(String[] args, int i) {
		if ((i >= args.length) || 
			(args[i].startsWith("-") && (args[i].length() > 1)) || 
			args[i].startsWith("\n")) {
			return true;
		}
		return false;
	}

    protected Collection<Node> getCollection(Node node) {
    	Collection<Node> nodes = new ArrayList<Node>();
    	nodes.add(node);
    	return nodes;    	
    }    
}
