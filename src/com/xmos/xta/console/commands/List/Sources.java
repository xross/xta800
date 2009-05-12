package com.xmos.xta.console.commands.List;

import com.xmos.dwarfhandler.DebugFile;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

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
		
        int nodeId = engine.getNodeId();
	    int coreId = engine.getCoreId();

	    DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(nodeId, coreId);
	    assert(debugInfo != null);        

	    for (DebugFile debugFile : debugInfo.getDebugEngine().getDebugFiles()) {
  	    	console.display(String.format("- %s (%s)", 
  	    			debugFile.getFileName(), debugFile.getBaseDir()));
  	    	  	    	
  	    	if (debugFile.getFile() == null) {
  	    		console.display(" (File not found)"); 	    		
  	    	} 
  	    	console.display("\n");  	
    	}   
    }
}
