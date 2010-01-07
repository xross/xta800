package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class LoopScope extends Command {	
	private String m_refString;
	private boolean m_absolute;

	public LoopScope() {
	}

	public LoopScope(String refString, boolean absolute) {
		m_refString = refString;
		m_absolute = absolute;
	}

    public int numArgs() {
    	return 2; 
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}    
    
    public String name() {
		return "loopscope"; 
    }        

    public String commandLine() {
       	return "loopscope " + " " + m_refString + " " + (m_absolute ? "absolute" : "relative");
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "loopscope <ANY_REF> <scope>: Adds the given loop scope define to the list of defines";
    	if (detailed) {
        	descString += "\n";
    		descString += "          scope: relative/r: Iteration number propagates to the enclosing path (Default)\n";                
    		descString += "          scope: absolute/a: Absolute number of iterations";
    	}
    	return descString;   		    	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) { 
			return null;
		}

    	boolean absolute;
	    if ((args[index + 2].compareTo("relative") == 0) ||  
	    	(args[index + 2].compareTo("r") == 0)){
	    	absolute = false;
	    	
	    } else if ((args[index + 2].compareTo("absolute") == 0) ||  
		         (args[index + 2].compareTo("a") == 0)){
	    	absolute = true;
	    	
	    } else {
	    	return null;
	    } 		 	    
        return new LoopScope(args[index + 1], absolute);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}
		
		engine.addLoopScope(getSrc(), m_refString, m_absolute);
    }    
 }