package com.xmos.xta.console.commands.Set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class LoopScope extends Command {	
	private int m_routeId;
	private int m_pathId;
	private boolean m_absolute;
	
	public LoopScope() {
		m_routeId = 0;
		m_pathId = 0;
		m_absolute = false;
	}
	
	public LoopScope(int routeId, int pathId, boolean absolute) {
		m_routeId = routeId;
		m_pathId = pathId;	
		m_absolute = absolute;
	}
			
    public int numArgs() {
    	return 3;
    }        

    public String name() {
    	return "loopscope";
    }        

    public String commandLine() {
    	return "loopscope " + 
    	    getRouteId(m_routeId) + " " + 
    	    m_pathId + " " + 
    	    (m_absolute ? "absolute" : "relative");
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "loopscope <route id> <path id> <scope>: Sets the scope of loop attached to a path";
    	if (detailed) {
        	descString += "\n";
    		descString += "          scope: relative/r: Iteration number propagates to the enclosing path (Default)\n";                
    		descString += "          scope: absolute/a: Absolute number of iterations";
    	}
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
	    	    	    
    	boolean absolute;
	    if ((args[index + 3].compareTo("relative") == 0) ||  
	    	(args[index + 3].compareTo("r") == 0)){
	    	absolute = false;
	    }
	    else if ((args[index + 3].compareTo("absolute") == 0) ||  
		         (args[index + 3].compareTo("a") == 0)){
	    	absolute = true;
	    }
	    else {
	    	return null;
	    }
    		  
	    
        return new LoopScope(routeId, pathId, absolute);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!engine.setLoopScope(m_routeId, m_pathId, m_absolute)) {
    		console.display(String.format("Error: Failed to set loop iterations: route id: %d path id: %d\n", m_routeId, m_pathId));    		
    	}
    }    
 }
