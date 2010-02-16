package com.xmos.xta.core.console.commands.scripter;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.scripter.Scripter;

public class Enable extends Command {
	private String m_reference;
	
	public Enable() {
	}

	public Enable(String reference) {
		m_reference = reference;
	}

    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "enable";
    }        

    public String commandLine() {
    	return "enable " + m_reference;
    }

    public String description(Engine engine, boolean detailed) {
        return "enable: Enable a mapping";      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) { 
			return null;
		}
	    
        return new Enable(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Scripter scripter = engine.getScripter();
		scripter.createRefs();
		scripter.enable(getSrc(), engine, m_reference);
    }
}

