package com.xmos.xta.core.console.commands.remove;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class PathTime extends Command {
	private String m_fromRef;
	private String m_toRef;

	public PathTime() {
	}
	
	public PathTime(String fromRef, String toRef) {
		m_fromRef = fromRef;
		m_toRef = toRef;
	}

    public int numArgs() {
   		return 2;
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ENDPOINT, ArgType.ENDPOINT};
	}    

    public String name() {
		return "pathtime"; 
    }        

    public String commandLine() {
       	return "pathtime " + m_fromRef + " " + m_toRef;        	
    }
    
    public String description(Engine engine, boolean detailed) {
		return "pathtime <from ENDPOINT_REF|*> <to ENDPOINT_REF|*>: Removes the given path time from the list of defines";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}
		
    	return new PathTime(args[index + 1], args[index + 2]);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		engine.removePathTime(getSrc(), m_fromRef, m_toRef);
    }    
 }
