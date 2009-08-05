package com.xmos.xta.console.commands.list;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class ThreadStopPoints extends Command {
	public ThreadStopPoints() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
  		return "threadstoppoints";
	}

	public String commandLine() {
   		return "threadstoppoints";
	}

	public String description(Engine engine, boolean detailed) {
        return "threadstoppoints: Lists the thread stop points";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
   		return new ThreadStopPoints();            
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

   		listThreadStopPoints(engine, console);        	
	}
	
	private void listThreadStopPoints(Engine engine, Console console) {
		if (engine.getThreadStopPoints().size() == 0){
			console.display("none\n");
			return;
		}
		
		for (long pc : engine.getThreadStopPoints()) {
    	    console.display(String.format("0x%x ", pc));
    	}		
	    console.display("\n");
	}
}
