package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Path extends Command {
	private String m_fromRef;
	private String m_toRef;
	
	Path() {
		m_fromRef = "";
		m_toRef = "";
	}
	
	Path(String fromRef, String toRef) {
		m_fromRef = fromRef;
		m_toRef = toRef;	  	
	}
	
    public int numArgs() {
    	return 2;
    }        

    public String name() {
    	return "path";
    }        

    public String commandLine() {
    	return "path " + m_fromRef + " " + m_toRef;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "path <from ref> <to ref>: Sets a path start and end points";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	String fromRef = args[index + 1];
        String toRef = args[index + 2];
        return new Path(fromRef, toRef);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        if (!engine.analyseRoute(m_fromRef, m_toRef)) {
        	console.display(String.format("Error: %s\n", engine.getErrorString()));
        }
    }   
 }
