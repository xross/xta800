package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

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
    	String descString = "loopscope <route id> <SRCLABEL_REF> <scope>: Sets the scope of the referenced loop";
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
    	try { routeId = getRouteId(args[index + 1]); }
	    catch (NumberFormatException e) {
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

		if (engine.getRoute(m_routeId) == null){
			console.display(String.format("Error: Failed to set loop scope: no such route id: %d\n", m_routeId));   
			return;
		}

		Reference ref = ReferenceFactory.createReference(m_refString, ReferenceClass.SRCLABEL_REF);
		if (!ref.resolve(engine, engine.getNodeId(), engine.getCoreId())) {
			console.display("Invalid code reference '" + m_refString + "'");
			return;
		}
		
		if (!engine.setLoopScope(m_routeId, ref.getPc(), m_absolute)) {
    		console.display(String.format("Error: Failed to set loop scope: route id: %d\n", m_routeId));    		
    	}
    }    
 }
