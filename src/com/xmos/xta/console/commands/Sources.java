package com.xmos.xta.console.commands;

import java.util.ArrayList;
import java.util.Collections;

import com.xmos.dwarfhandler.DebugFile;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Sources extends Command {
	Sources() {
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

    public Command clone(String[] args, int index, boolean commandLineMode) {
        return new Sources();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;		
		
        int nodeId = engine.getNodeId();
	    int coreId = engine.getCoreId();
	    DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(nodeId, coreId);
	    assert(debugInfo != null);        
		ArrayList<String> files = new ArrayList<String>();
  	    for (DebugFile debugFile : debugInfo.getDebugEngine().getDebugFiles()) {
  	    	String file = String.format("- %s (%s)", 
  	    			debugFile.getFileName(), debugFile.getCompilationDir());
  	    	  	    	
  	    	if (debugFile.getFile() == null) {
  	    		file += " (File not found)"; 	    		
  	    	} 
  	    	files.add(file); 	    	
    	}   
  	    Collections.sort(files);
  	    for (String file : files){
  	    	console.display(file + "\n"); 
  	    }
    }
}
