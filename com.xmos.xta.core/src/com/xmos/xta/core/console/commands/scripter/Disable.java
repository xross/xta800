package com.xmos.xta.core.console.commands.scripter;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.scripter.Scripter;

public class Disable extends Command {
	private String m_reference;
	
	public Disable() {
	}

	public Disable(String reference) {
		m_reference = reference;
	}

    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "disable";
    }        

    public String commandLine() {
    	return "disable " + m_reference;
    }

    public String description(Engine engine, boolean detailed) {
        return "disable: Disable a mapping";      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) { 
			return null;
		}
	    
        return new Disable(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Scripter scripter = engine.getScripter();
		scripter.createRefs();
		scripter.disable(getSrc(), engine, m_reference);
    }
}

