package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class LoopPath extends Command {	
	private String m_refString;
	private int m_iterations;
	
	public LoopPath() {
		m_refString = "";
		m_iterations = 0;
	}
	
	public LoopPath(String refString, int iterations) {
		m_refString = refString;
		m_iterations = iterations;
	}

    public int numArgs() {
   		return 2; 
    }        

    public String name() {
    	return "looppath";
    }        

    public String commandLine() {
       	return "looppath " + " " + m_refString + " " + m_iterations;        	
    }
    
    public String description(Engine engine, boolean detailed) {
   		return "looppath <SRCLABEL_REF> <iterations>: Sets the number of iterations for the path identified";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

   	    int iterations;
       	try { iterations = Integer.parseInt(args[index + 2]); }
   	    catch (NumberFormatException e) {
   	        return null;
   	    }    		      	    
        return new LoopPath(args[index + 1], iterations);    		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

   		if (!engine.setLoopPathIterations(m_refString, m_iterations)) {
       		console.display(String.format(
       				"Error: Failed to set loop path iterations on reference: %s\n", 
       				m_refString));    		
    	}    					
    }    
 }
