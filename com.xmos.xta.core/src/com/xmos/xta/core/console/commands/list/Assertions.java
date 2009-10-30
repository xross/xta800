package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.assertions.AssertionInfo;

public class Assertions extends Command {
	public Assertions() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
  		return "assertions";
	}

	public String commandLine() {
   		return "assertions";
	}

	public String description(Engine engine, boolean detailed) {
        return "assertions: Lists the assertions";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
   		return new Assertions();            
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

       	listAssertions(engine, console);        	
	}

	private void listAssertions(Engine engine, Console console) {
		Collection<AssertionInfo> infos = engine.getAssertions();
	    if (infos.isEmpty()) {
	    	console.display("none\n");
	    	return;
	    }
		for (AssertionInfo info : infos) {
			console.display(info.m_description + " " + info.m_value + "\n");
		}	    
	}
}
