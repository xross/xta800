package com.xmos.xta.console.commands.set;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Loop extends Command {	
	private String m_refString;
	private int m_iterations;
	
	public Loop() {
		m_refString = "";
		m_iterations = 0;
	}
	
	public Loop(String refString, int iterations) {
		m_refString = refString;
		m_iterations = iterations;
	}
	
    public int numArgs() {
   		return 2; 
    }        

    public String name() {
    	return "loop";
    }        

    public String commandLine() {
       	return "loop " + " " + m_refString + " " + m_iterations;        	
    }
    
    public String description(Engine engine, boolean detailed) {
   		return "loop <SRCLABEL_REF> <iterations>: Sets the number of iterations for the loop identified";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

   	    int iterations;
       	try { iterations = Integer.parseInt(args[index + 2]); }
   	    catch (NumberFormatException e) {
   	        return null;
   	    }    		      	    
        return new Loop(args[index + 1], iterations);    		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

   		if (!engine.setLoopIterations(m_refString, m_iterations)) {
       		console.display(String.format(
       				"Error: Failed to set loop iterations on reference: %s\n", 
       				m_refString));
   		}
    }    
 }
