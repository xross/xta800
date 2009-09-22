package com.xmos.xta.core.console.commands.list;

import com.xmos.dwarfhandler.DebugFile;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

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
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;		
		
	    DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(engine.getNodeId(), engine.getCoreId());
	    assert(debugInfo != null);        

	    for (DebugFile debugFile : debugInfo.getDebugEngine().getDebugFiles()) {

	    	console.display(String.format("compilation dir: %s, filename: %s", 
	    			debugFile.getBaseDir(), debugFile.getFileName()));

  	    	if (debugFile.getFile() == null) {
  	    		console.display(" (File not found)"); 	    		
  	    	} 
  	    	console.display("\n");
    	}   
    }
}
