package com.xmos.xta.console.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public abstract class Command {
    public enum ArgType {
    	FILE,
    	FUNCTION,
    	ALLREFS,
    	ENDPOINT,
    	LABEL,
    	HELP,
    	IGNORE,
    }

    private CommandSource m_src;
    
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
	
    public boolean isExecutableLoaded(Engine engine, Console console) {    
	    File executable = engine.getExecutable();
    	if (executable == null) {
    		engine.notify(getSrc(), Notifier.NOTIFY_ERROR, "no executable loaded");
	        return false;
    	}
    	return true;
    }

    public boolean isPresent(Engine engine, int nodeId, int coreId, Console console) { 
    	if (engine.getXeLoader().getMemoryImage(nodeId, coreId) == null) {
    		engine.notify(m_src, Notifier.NOTIFY_ERROR,
    				String.format("no code loaded on node: %d, core: %d", nodeId, coreId));
    		return false;
    	}
    	return true;    	
    }
    
    public boolean validateRoutes(Engine engine, Console console) {
       	if (engine.getRoutes().size() == 0) {
    		engine.notify(m_src, Notifier.NOTIFY_ERROR,
    				"no routes analysed");
    		return false;
     	}
        return true;  	
    }

    public boolean validateRouteId(Engine engine, Console console, int routeId) {
    	if (routeId != Engine.ROUTE_ID_GLOBAL) {
    	    if (engine.getRoutes().size() == 0) {
       			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, "no routes analysed");
    		    return false;
    	    }
        	Collection<Route> routes = engine.getRoute(routeId);
        	if (routes.size() == 0) {
       			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
       					"invalid route id: %d", routeId));
        		return false;
        	}
    	}
    	return true;
    }
    
    public boolean validateNodeId(Engine engine, Console console, int routeId, int nodeId) {
    	if (routeId == Engine.ROUTE_ID_GLOBAL) {
        	Node node = engine.getStructureGraph().getNode(nodeId);
        	if (node == null) {
       			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
       					"invalid node id: %d", nodeId));
        		return false;    		
        	}        	    		    		
    	} else {
	    	if (engine.getRoutes().size() == 0) {
	   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, "no routes analysed");
	    		return false;
	    	}
        	Collection<Route> routes = engine.getRoute(routeId);
        	if (routes.size() == 0) {
       			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
       					"invalid route id: %d", routeId));
        		return false;
        	}
        	
        	boolean found = false;
        	for (Route route : routes) {
            	Node node = route.getNode(nodeId);
            	if (node != null) {
            		found = true;
            		break;
            	}        	
        	}
        	if (!found) {
       			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
       					"invalid node id: %d", nodeId));
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

    public void setSrc(CommandSource src) {
		m_src = src;
	}
    
    public CommandSource getSrc() {
		return m_src;
	}
}
