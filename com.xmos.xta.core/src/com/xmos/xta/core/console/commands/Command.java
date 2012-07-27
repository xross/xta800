package com.xmos.xta.core.console.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public abstract class Command {
	public static final String REDIRECT_OVERWITE = ">";
	public static final String REDIRECT_APPEND   = ">>";

    public enum ArgType {
    	FILE,
    	FUNCTION,
    	ALLREFS,
    	ENDPOINT,
    	LABEL,
    	HELP,
    	HELPCOMMAND,
    	IGNORE,
    }
    public enum RedirectMode {
    	OVERWRITE,
    	APPEND,
    }

    private CommandSource m_src;
    private String m_fileRedirect;
    private RedirectMode m_fileRedirectMode;
	private String[] m_commandNames;

    public abstract int numArgs();
    public abstract String commandLine();
    public abstract String description(Engine engine, boolean detailed);
    public abstract Command clone(String[] args, int index);
    public abstract void execute(Shell shell, Engine engine, Console console);

    public Command(String[] commandNames) {
    	m_commandNames = commandNames;
    }
    
    public String name() {
    	return m_commandNames[0];
    }
   
    public boolean matches(String arg) {
    	for (String command : m_commandNames) {
    		if (command.equals(arg)) {
    			return true;
    		}
    	}
    	return false;
    }

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

    public boolean validateRoutes(Engine engine, Console console) {
       	if (engine.getRoutes().size() == 0) {
    		engine.notify(m_src, Notifier.NOTIFY_ERROR,
    				"no routes analyzed");
    		return false;
     	}
        return true;
    }

    public boolean validateRouteId(Engine engine, Console console, int routeId) {
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

    	return true;
    }

    public boolean validateNodeId(Engine engine, Console console, int routeId, int nodeId) {
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

    	boolean found = true;
    	for (Route route : routes) {
        	Node node = route.getNode(nodeId);
        	if (node == null) {
        		found = false;
        	}
    	}
    	if (!found) {
   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
   					"invalid node id: %d", nodeId));
    		return false;
    	}
    	return true;
    }

    public static int getRouteId(String routeId) {
    	if (routeId.equals("-")) {
    		return Engine.ROUTE_ID_LAST;
    	}else if (routeId.equals("*")) {
    		return Engine.ROUTE_ID_ALL;
    	}
    	int route = Integer.parseInt(routeId);
    	return route;
    }

    public static String getRouteId(int routeId) {
    	if (routeId == Engine.ROUTE_ID_LAST) {
    		return "-";
    	} else if (routeId == Engine.ROUTE_ID_ALL) {
    		return "*";
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
			args[i].startsWith("\n") ||
			args[i].equals(REDIRECT_OVERWITE) ||
			args[i].equals(REDIRECT_APPEND)) {
			return true;
		}
		return false;
	}

    protected Collection<Node> getCollection(Node node) {
    	Collection<Node> nodes = new ArrayList<Node>();
    	nodes.add(node);
    	return nodes;
    }

    protected Node getReferencedNode(Engine engine, int routeId, String nodeString) {
    	Collection<Route> routes = engine.getRoute(routeId);
    	for (Route route : routes) {
    		Node node = route.getNode(nodeString);
    		if (node != null) {
    			return node;
    		}
    	}
    	return null;
    }

    public void setSrc(CommandSource src) {
		m_src = src;
	}

    public CommandSource getSrc() {
		return m_src;
	}

    public void setFileRedirect(String fileRedirect) {
    	m_fileRedirect = fileRedirect;
    }

    public String getFileRedirect() {
    	return m_fileRedirect;
    }

    public void setFileRedirectMode(RedirectMode fileRedirectMode) {
    	m_fileRedirectMode = fileRedirectMode;
    }

    public RedirectMode getFileRedirectMode() {
    	return m_fileRedirectMode;
    }

    public String getFullCommandLine() {
		if (m_fileRedirect != null) {
			switch (m_fileRedirectMode) {
			case OVERWRITE:
				return commandLine() + " " + REDIRECT_OVERWITE + " " + m_fileRedirect;

			case APPEND:
				return commandLine() + " " + REDIRECT_APPEND + " " + m_fileRedirect;

			default:
				return commandLine();
			}
		}
		return commandLine();
    }
    
    public CoreId getCoreId(Engine engine, String coreReference) {
    	return engine.getCoreId(getSrc(), coreReference);
    }
}

