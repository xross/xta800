package com.xmos.xta.console.commands.analyse;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Path extends Command {
	private String m_fromRef;
	private String m_toRef;
	
	public Path() {
		m_fromRef = "";
		m_toRef = "";
	}
	
	public Path(String fromRef, String toRef) {
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
    	return "path";
    }        

    public String commandLine() {
    	return "path " + m_fromRef + " " + m_toRef;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "path <from ENDPOINT_REF> <to ENDPOINT_REF>: Sets a path start and end points";      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	String fromRef = args[index + 1];
        String toRef = args[index + 2];
        return new Path(fromRef, toRef);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        engine.analyseRoute(getSrc(), m_fromRef, m_toRef);
    }    
 }
