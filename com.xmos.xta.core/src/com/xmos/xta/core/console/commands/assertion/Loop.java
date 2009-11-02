package com.xmos.xta.core.console.commands.assertion;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

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

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}    
    
    public String name() {
    	return "loop";
    }        

    public String commandLine() {
       	return "loop " + " " + m_refString + " " + m_iterations;        	
    }
    
    public String description(Engine engine, boolean detailed) {
   		return "loop <ANY_REF> <iterations>: Assert the number of iterations for the loop identified";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

		try {
	   	    int iterations = Integer.parseInt(args[index + 2]);
	        return new Loop(args[index + 1], iterations);    		
	        
		} catch (NumberFormatException e) {
	   	    return null;
	   	}    		   	    		
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    engine.assertLoop(getSrc(), m_refString, m_iterations);
    }    
 }