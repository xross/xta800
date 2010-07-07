package com.xmos.xta.core.console.commands.list;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;

public class Endpoints extends Command {
	private static final String[] COMMAND_NAMES = { "endpoints" };

	public Endpoints() {
		super(COMMAND_NAMES);
	}

	public int numArgs() {
        return 0;
	}

	public String commandLine() {
   		return name();
	}

	public String description(Engine engine, boolean detailed) {
        return name() + ": Lists the endpoints";
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

	private void listEndpoints(final Engine engine, final Console console) {
		for (CoreId activeCore : engine.getActiveCores()) {			
			for (CodePointInfo info : engine.getEndpoints(activeCore)) {
	    	    console.display(String.format("%s, label: %s, pc: 0x%x", 
	    	    		activeCore.toString(), 
	    	    		info.getLabel(), info.getPc()));

    	    	console.display(", compilation dir: " + info.getCompilationDir());
    	    	console.display(", filename: " + info.getFileName());
    	    	console.display(", line number: " + info.getLineNumber());
	    	    console.display("\n");
	    	}				
		}
	}
}
