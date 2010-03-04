package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.utils.NodeCorePair;

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

	private void listEndpoints(final Engine engine, final Console console) {
		for (NodeCorePair activeCore : engine.getActiveCores()) {			
			for (CodePointInfo info : engine.getEndpoints(activeCore.getNode(), activeCore.getCore())) {
	    	    console.display(String.format("node: %d, core: %d, label: %s, pc: 0x%x", 
	    	    		activeCore.getNode(), activeCore.getCore(), 
	    	    		info.getLabel(), info.getPc()));

	    	    if (!info.getCompilationDir().equals("")) {
	    	    	console.display(", compilation dir: " + info.getCompilationDir());
	    	    	console.display(", filename: " + info.getFileName());
	    	    	console.display(", line number: " + info.getLineNumber());
	    	    }
	    	    console.display("\n");
	    	}				
		}
	}
}
