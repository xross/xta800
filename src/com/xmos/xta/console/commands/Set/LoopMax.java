package com.xmos.xta.console.commands.Set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class LoopMax extends Command {	
	private String m_refString;
	private int m_iterations;
	private int m_routeId;
	
	public LoopMax() {
		m_refString = "";
		m_iterations = 0;
		m_routeId = 0;
	}
	
	public LoopMax(int routeId, String refString, int iterations) {
		m_refString = refString;
		m_iterations = iterations;
		m_routeId = routeId;
	}
			
    public int numArgs() {
    	return 3;
    }        

    public String name() {
    	return "loopmax";
    }        

    public String commandLine() {
    	return "loopmax " + 
    		getRouteId(m_routeId) + " " + 
    		m_refString + " " + 
    	    m_iterations;
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "loopmax <route id> <loop SRCLABEL_REF> <iterations>: Sets the maximum number of iterations for a loop identified";
    	return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

	    int routeId;
    	try { routeId = getRouteId(args[index + 1]); }
	    catch (NumberFormatException e) {
	        return null;
	    }	    	    

	    int iterations;
    	try { iterations = Integer.parseInt(args[index + 3]); }
	    catch (NumberFormatException e) {
	        return null;
	    }    		  
	    
        return new LoopMax(routeId, args[index + 2], iterations);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
		if (engine.getRoute(m_routeId) == null){
			console.display(String.format("Error: Failed to set loop iterations: no such route id: %d\n", m_routeId));   
			return;
		}

		Reference ref = ReferenceFactory.createReference(m_refString, ReferenceClass.SRCLABEL_REF);
		if (!ref.resolve(engine, engine.getNodeId(), engine.getCoreId())) {
			console.display("Invalid code reference '" + m_refString + "'");
			return;
		}
		
		if (!engine.setLoopMax(m_routeId, ref.getPc(), m_iterations)) {
    		console.display(String.format("Error: Failed to set max loop iterations: route id: %d\n", m_routeId));    		
    	}

    }    
 }
