package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class Loop extends Command {	
	private int m_routeId;
	private String m_refString;
	private int m_iterations;
	
	public Loop() {
		m_routeId = 0;
		m_refString = "";
		m_iterations = 0;
	}
	
	public Loop(int routeId, String refString, int iterations) {
		m_routeId = routeId;
		m_refString = refString;
		m_iterations = iterations;
	}
	
    public int numArgs() {
   		return 3; 
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.ALLREFS};
	}    
    
    public String name() {
    	return "loop";
    }        

    public String commandLine() {
       	return "loop " + getRouteId(m_routeId) + " " + m_refString + " " + m_iterations;        	
    }
    
    public String description(Engine engine, boolean detailed) {
   		return "loop <route id> <ANY_REF> <iterations>: Sets the number of iterations for the loop identified";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

		try {
			int routeId = getRouteId(args[index + 1]);
	   	    int iterations = Integer.parseInt(args[index + 3]);
	        return new Loop(routeId, args[index + 2], iterations);    		
	        
		} catch (NumberFormatException e) {
	   	    return null;
	   	}    		   	    		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		Node node = getReferencedNode(engine, m_routeId, m_refString);
		if (node != null) {
   		    engine.setLoopIterations(getSrc(), node, m_iterations);
			
		} else {		
   		    engine.setLoopIterations(getSrc(), m_routeId, m_refString, m_iterations);
		}
    }    
 }
