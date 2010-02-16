package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class From extends Command {
	private String m_fromRef;
	
	public From() {
		m_fromRef = "";
	}
	
	public From(String fromRef) {
		m_fromRef = fromRef;
	}

    public int numArgs() {
   		return 1; 
    }

    public String name() {
    	return "from";
    }        

    public String commandLine() {
       	return String.format("from %s", m_fromRef);
    }
    
    public String description(Engine engine, boolean detailed) {
       	return "from <ENDPOINT_REF>: Sets the from endpoint";
    }

    public Command clone(String[] args, int index) {
    	if ((index + numArgs()) >= args.length) 
			return null;

	    return new From(args[index + 1]);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

        engine.setFromEndPoint(getSrc(), m_fromRef);
    }    
}
