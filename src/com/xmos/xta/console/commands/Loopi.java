package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Loopi extends Command {
	int m_pathId;
	boolean m_absolute;
	int m_iterations;
	
	Loopi() {
		m_pathId = 0;
		m_absolute = false;
		m_iterations = 0;
	}
	
	Loopi(int pathId, boolean absolute, int iterations) {
		m_pathId = pathId;	
		m_absolute = absolute;
		m_iterations = iterations;
	}
	
    public int numArgs() {
    	return 3;
    }        

    public String name() {
    	return "loopi";
    }        

    public String commandLine() {
    	String absStr = m_absolute ? "ABSOLUTE" : "RELATIVE";
    	return "loopi " + 
    	    m_pathId + " " + 
    	    absStr + " " + 
    	    m_iterations;
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "loopi <path instance> <scope> <iterations>: Sets the number of iterations for a path instance"; 
    	if (detailed) {
        	descString += "\n";
    		descString += "      scope: RELATIVE/r: Iteration number propagates to the enclosing path (Default)\n";                
    		descString += "      scope: ABSOLUTE/a: Absolute number of iterations";                
    	}
    	return descString;
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		
	    
    	int pathId;
    	try { pathId = Integer.parseInt(args[index + 1]); }
	    catch (NumberFormatException e) {
	        return null;
	    }

	    boolean absolute;
	    if ((args[index + 2].compareTo("RELATIVE") == 0) ||  
	    	(args[index + 2].compareTo("r") == 0)){
	    	absolute = false;
	    }
	    else if ((args[index + 2].compareTo("ABSOLUTE") == 0) ||  
		         (args[index + 2].compareTo("a") == 0)){
	    	absolute = true;
	    }
	    else {
	    	return null;
	    }
	    	    	    
    	int iterations;
    	try { iterations = Integer.parseInt(args[index + 3]); }
	    catch (NumberFormatException e) {
	        return null;
	    }    		  
	    
        return new Loopi(pathId, absolute, iterations);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!engine.setLoopIterations(m_pathId, false, m_absolute, m_iterations)) {
    		console.display(String.format("Error: Failed to set loop iterations: path instance id: %d\n", m_pathId));    		
    	}
    }    
 }
