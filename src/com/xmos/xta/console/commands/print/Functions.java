package com.xmos.xta.console.commands.print;

import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;

public class Functions extends Command {
	private int m_routeId;
	
	public Functions() {
		m_routeId = 0;
	}
	
	public Functions(int routeId) {
		m_routeId = routeId;		
	}
	
    public int numArgs()        { return 1; }        
    public String name()        { return "functions"; }        
    public String commandLine() { return "functions " + getRouteId(m_routeId); }        
    
    public String description(Engine engine, boolean detailed) {
    	return "functions <route id>: Shows functions this route touches";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		
    	try {
     		return new Functions(getRouteId(args[index + 1]));
	    } catch (NumberFormatException e) {
	        return null;
	    }    	
  }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!validateRouteId(engine, console, m_routeId))
			return;		    	

    	displayFunctions(engine, console);
    }
	
    private void displayFunctions(Engine engine, Console console) {
       	for (String function : getFunctions(engine)) {
       		console.display(String.format("- %s\n", function));
        }
    }

    private Set<String> getFunctions(Engine engine) {
    	final Set<String> functions = new TreeSet<String>();   
		Node node = engine.getRoute(m_routeId).getNode();
	    node.traverse(0, new DefaultNodeTraverser() {
			public boolean foundFunctionNode(Node node, int depth) {
				functions.add(node.getFunctionName());
				return true;
			}
	    });
    	return functions;
    }
}
