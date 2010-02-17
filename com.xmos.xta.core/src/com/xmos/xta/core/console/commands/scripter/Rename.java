package com.xmos.xta.core.console.commands.scripter;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.scripter.Scripter;

public class Rename extends Command {
	private String m_from;
	private String m_to;
	
	public Rename() {
	}

	public Rename(String from, String to) {
		m_from = from;
		m_to = to;
	}

    public int numArgs() {
    	return 2;
    }        

    public String name() {
    	return "rename";
    }        

    public String commandLine() {
    	return "rename " + m_from + " " + m_to;
    }

    public String description(Engine engine, boolean detailed) {
        return "rename: Rename a mapping";      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) { 
			return null;
		}
	    
        return new Rename(args[index + 1], args[index + 2]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Scripter scripter = engine.getScripter();
		scripter.createRefs();
		scripter.rename(getSrc(), engine, m_from, m_to);
    }
}

