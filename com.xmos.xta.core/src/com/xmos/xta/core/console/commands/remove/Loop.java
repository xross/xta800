package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class Loop extends Command {	
	private String m_refString;
	
	public Loop() {
	}
	
	public Loop(String refString) {
		m_refString = refString;
	}
	
    public int numArgs() {
   		return 1;
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}    
    
    public String name() {
		return "loop"; 
    }        

    public String commandLine() {
       	return "loop " + " " + m_refString;        	
    }
    
    public String description(Engine engine, boolean detailed) {
		return "loop <ANY_REF|*>: Removes the given loop count define to the list of defines";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) { 
			return null;
		}

    	return new Loop(args[index + 1]);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}
	    
		engine.removeLoop(getSrc(), m_refString);
    }
 }