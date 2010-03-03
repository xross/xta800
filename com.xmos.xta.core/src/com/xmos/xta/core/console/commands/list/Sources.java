package com.xmos.xta.core.console.commands.list;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class Sources extends Command {
	public Sources() {
	}

    public int numArgs() {
    	return 0;
    }

    public String name() {
    	return "sources";
    }

    public String commandLine() {
    	return "sources";
    }

    public String description(Engine engine, boolean detailed) {
        return "sources: Lists the source files";
    }

    public Command clone(String[] args, int index) {
        return new Sources();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (NodeCorePair activeCore : engine.getActiveCores()) {			
		    DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(activeCore.getNode(), activeCore.getCore());
		    assert(debugInfo != null);

		    for (DebugFile debugFile : debugInfo.getDebugEngine().getDebugFiles()) {

		    	console.display(String.format("node: %d, core %d, compilation dir: %s, filename: %s",
		    			activeCore.getNode(), activeCore.getCore(), debugFile.getBaseDir(), debugFile.getFileName()));

	  	    	if (debugFile.getFile() == null) {
	  	    		console.display(" (File not found)");
	  	    	}
	  	    	console.display("\n");
	    	}				
		}
    }
}
