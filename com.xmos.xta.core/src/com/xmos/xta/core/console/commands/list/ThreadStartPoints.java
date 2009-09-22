package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

public class ThreadStartPoints extends Command {
	public ThreadStartPoints() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
   		return "threadstartpoints";
	}

	public String commandLine() {
   		return "threadstartpoints";
	}

	public String description(Engine engine, boolean detailed) {
        return "threadstartpoints: Lists the thread start points";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
  		return new ThreadStartPoints();            
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

   		listThreadStartPoints(engine, console);        	
	}
	
	private void listThreadStartPoints(Engine engine, Console console) {
		if (engine.getThreadStartPoints().size() == 0){
			console.display("none\n");
			return;
		}
		
		for (long pc : engine.getThreadStartPoints()) {
    	    console.display(String.format("0x%x ", pc));
    	}		
	    console.display("\n");
	}
}
