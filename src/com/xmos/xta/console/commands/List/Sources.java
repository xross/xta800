package com.xmos.xta.console.commands.List;

import java.util.Set;
import java.util.TreeSet;

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

	    Set<String> sourceNames = new TreeSet<String>();
	    for (DebugFile debugFile : debugInfo.getDebugEngine().getDebugFiles()) {

	    	String sourceName = String.format("- %s (%s)", 
  	    			debugFile.getFileName(), debugFile.getBaseDir());
	    	  	    	  	    	

  	    	if (debugFile.getFile() == null) {
  	    		sourceName += " (File not found)"; 	    		
  	    	} 
  	    	sourceNames.add(sourceName + "\n");
    	}   
	    
	    // The output is now sorted...
	    for (String sourceName : sourceNames) {
	        console.display(sourceName);
	    }
    }
}
