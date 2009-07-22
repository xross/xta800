package com.xmos.xta.console.commands.list;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointInfo;

public class Endpoints extends Command {
	public Endpoints() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
   		return "endpoints";
	}

	public String commandLine() {
   		return "endpoints";
	}

	public String description(Engine engine, boolean detailed) {
        return "endpoints: Lists the endpoints";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
   		return new Endpoints();            
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

       	listEndpoints(engine, console);        	
	}
	
	private void listEndpoints(Engine engine, Console console) {
		if (engine.getEndpoints().entrySet().size() == 0){
			console.display("none\n");
			return;
		}

		for (CodePointInfo info : engine.getEndpoints().values()) {
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
