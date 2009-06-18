package com.xmos.xta.console.commands.list;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;

public class Knowns extends Command {
	private int m_routeId;

	public Knowns() {
	}

	public Knowns(int routeId) {
		m_routeId = routeId;
	}
	
    public int numArgs() {
   		return 1;
    }

    public String name() {
    	return "knowns";
    }        

    public String commandLine() {
   		return "knowns " + getRouteId(m_routeId);
    }
    
    public String description(Engine engine, boolean detailed) {
        return "knowns <route id>: Displays the list of knowns set for the given route";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;    	

    	try {
    	    return new Knowns(getRouteId(args[index + 1]));
	    } catch (NumberFormatException e) {
	        return null;
	    }    	
    }
    
    public void execute(Shell shell, Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

	    if (!validateRouteId(engine, console, m_routeId))
		    return;
		
	    Collection<Node> nodes = engine.getRoute(m_routeId).getUnknowns(UnknownState.KNOWN);
	    if (nodes.size() > 0) {
			for (Node node : nodes) {			
			    console.display(node.getAttributes().getUnknownDescription() + "\n");
			}			    	
	    } else {
		    console.display("none\n");
		}
    }   
}
