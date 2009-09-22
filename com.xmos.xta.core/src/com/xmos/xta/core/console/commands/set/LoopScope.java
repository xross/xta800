package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class LoopScope extends Command {	
	private int m_routeId;
	private String m_refString;
	private boolean m_absolute;
	
	public LoopScope() {
		m_routeId = 0;		
		m_refString = "";
		m_absolute = false;
	}
	
	public LoopScope(int routeId, String refString, boolean absolute) {
		m_routeId = routeId;		
		m_refString = refString;
		m_absolute = absolute;
	}

    public int numArgs() {
    	return 3;
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.ALLREFS, ArgType.IGNORE};
	}    
    
    public String name() {
    	return "loopscope";
    }        

    public String commandLine() {
    	return "loopscope " + 
    	    getRouteId(m_routeId) + " " +
    	    m_refString + " " + 
    	    (m_absolute ? "absolute" : "relative");
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "loopscope <route id> <ANY_REF> <scope>: Sets the scope of the referenced loop";
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
    	try { routeId = getRouteId(args[index + 1]);
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
        return new LoopScope(routeId, args[index + 2], absolute);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
		
		engine.setLoopScope(getSrc(), m_routeId, m_refString, m_absolute);
    }    
 }
