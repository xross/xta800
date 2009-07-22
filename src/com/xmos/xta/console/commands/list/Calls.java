package com.xmos.xta.console.commands.list;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointInfo;

public class Calls extends Command {
	public Calls() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
  		return "calls";
	}

	public String commandLine() {
   		return "calls";
	}

	public String description(Engine engine, boolean detailed) {
        return "calls: Lists the calls";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
   		return new Calls();            
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

       	listCalls(engine, console);        	
	}
	
	private void listCalls(Engine engine, Console console) {
		if (engine.getCalls().entrySet().size() == 0){
			console.display("none\n");
			return;
		}

		for (CodePointInfo info : engine.getCalls().values()) {
    	    console.display(String.format("Label: %s, pc: 0x%x", info.getLabel(), info.getPc())); 
    	    
    	    if (!info.getCompilationDir().equals("")) {
    	    	console.display(", compilation dir: " + info.getCompilationDir());
    	    	console.display(", filename: " + info.getFileName());
    	    	console.display(", line number: " + info.getLineNumber());
    	    }
    	    console.display("\n");    	    
    	}				
	}
}
