package com.xmos.xta.console.commands.Set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Loop extends Command {	
	private int m_routeId;
	private int m_pathId;
	private int m_iterations;
	
	public Loop() {
		m_routeId = 0;
		m_pathId = 0;
		m_iterations = 0;
	}
	
	public Loop(int routeId, int pathId, int iterations) {
		m_routeId = routeId;
		m_pathId = pathId;	
		m_iterations = iterations;
	}
			
    public int numArgs() {
    	return 3;
    }        

    public String name() {
    	return "loop";
    }        

    public String commandLine() {
    	return "loop " + 
    		getRouteId(m_routeId) + " " + 
    	    m_pathId + " " + 
    	    m_iterations;
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "loop <route id> <path id> <iterations>: Sets the number of iterations for the given route/path";
    	return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int routeId;
    	int pathId;
    	try { 
    		routeId = getRouteId(args[index + 1]); 
    		pathId = getId(args[index + 2]);
    	} catch (NumberFormatException e) {
	        return null;
	    }
	    	    	    
    	int iterations;
    	try { iterations = Integer.parseInt(args[index + 3]); }
	    catch (NumberFormatException e) {
	        return null;
	    }    		  
	    
        return new Loop(routeId, pathId, iterations);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!engine.setLoopIterations(m_routeId, m_pathId, m_iterations)) {
    		console.display(String.format("Error: Failed to set loop iterations: route id: %d path id: %d\n", m_routeId, m_pathId));    		
    	}
    }    
 }
