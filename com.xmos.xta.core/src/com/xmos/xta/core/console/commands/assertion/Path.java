package com.xmos.xta.core.console.commands.assertion;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;

public class Path extends Command {
	private String m_fromRef;
	private String m_toRef;
	private TimeUnit m_timeUnit;
	
	public Path() {
		m_fromRef = "";
		m_toRef = "";
	}
	
	public Path(String fromRef, String toRef, TimeUnit timeUnit) {
		m_fromRef = fromRef;
		m_toRef = toRef;	  	
		m_timeUnit = timeUnit;
	}
	
    public int numArgs() {
    	return 4;
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ENDPOINT, ArgType.ENDPOINT};
	}    

    public String name() {
    	return "path";
    }        

    public String commandLine() {
    	return "path " + m_fromRef + " " + m_toRef + " " + m_timeUnit.getDescription(false);
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "path <from ENDPOINT_REF> <to ENDPOINT_REF> <value> <mode>: Assert timing requirement for the given path";      	
       	if (detailed) {    	    	
       		descString += "\n" + TimeUnit.getHelp();
       	}
        return descString;    
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	String fromRef = args[index + 1];
        String toRef   = args[index + 2];
    	String value   = args[index + 3];
   		String mode    = args[index + 4];

	    TimeUnit timeUnit = new TimeUnit(value, mode);
	    if (timeUnit.isValid()) {
	    	return new Path(fromRef, toRef, timeUnit);
	    }
	    return null;                
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        engine.assertRoute(getSrc(), m_fromRef, m_toRef, m_timeUnit);
    }    
 }
