package com.xmos.xta.core.console.commands.assertion;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.TimeUnit;

public class Time extends Command {
	private String m_refString;
	private TimeUnit m_timeUnit;
	
	public Time() {
		m_refString = null;
		m_timeUnit = null;
	}
	
	public Time(String refString, TimeUnit timeUnit) {
		m_refString = refString;
		m_timeUnit = timeUnit;
	}

    public int numArgs() {
   		return 3; 
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ENDPOINT};
	}    
    
    public String name() {
    	return "time";
    }        

    public String commandLine() {
       	return String.format("time %s %s", m_refString, m_timeUnit.getDescription(false));
    }
    
    public String description(Engine engine, boolean detailed) {
        String descString = "time <ENDPOINT_REF> <value> <mode>: Asserts the time taken for the instruction at the given pc";         
       	if (detailed) {    	    	
           	descString += "\n";
           	descString += TimeUnit.getHelp();
       	}
       	return descString;
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length) 
			return null;

    	String ref = args[index + 2];   		
    	String value = args[index + 3];
   		String mode = args[index + 4];
   		    
	    TimeUnit timeUnit = new TimeUnit(value, mode);
	    if (timeUnit.isValid()) {
	    	return new Time(ref, timeUnit);
	    }
	    return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	    	

		engine.assertFixedTime(getSrc(), m_refString, m_timeUnit);
    }    
}
