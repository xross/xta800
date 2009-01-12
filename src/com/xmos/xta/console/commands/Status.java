package com.xmos.xta.console.commands;

import java.io.File;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Progress;
import com.xmos.xta.engine.Route;

class Status extends Command {
	Status() {
	}
	
    public int numArgs() {
    	return 0;
    }        
	
    public String name() {
    	return "status";
    }        
	
    public String commandLine() {
    	return "status";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "status: Display current status";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        return new Status();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	int nodeId = engine.getNodeId();
    	int coreId = engine.getCoreId();
    	File executable = engine.getExecutable();
    	    	
    	console.display(String.format("Current node: %d\n", nodeId));    	
    	console.display(String.format("Current core: %d\n", coreId));

    	console.display(String.format("Frequency: %d\n", engine.getConfig().m_frequency));    	
    	console.display(String.format("NumThreads: %d\n", engine.getConfig().m_numThreads));
    	
    	if (engine.getResolve())
    		console.display(String.format("Mode: Resolve\n"));
    	else
    		console.display(String.format("Mode: No Resolve\n"));    		
    	
    	if (executable==null)
    		console.display(String.format("No executable loaded\n"));
    	else
    		console.display(String.format("Loaded executable: %s\n", executable.getAbsolutePath()));
    	
    	for (MemoryImage memoryImage : engine.getMemoryImages()) {
    		int node = memoryImage.getNodeId();
    		int core = memoryImage.getCoreId();    		
    		console.display(String.format("Node: %d, Core: %d ", node, core));

    		DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(node, core);    		    		
    		if (debugInfo.getDebugEngine().hasDebugInfo())
    			console.display(String.format("(Debug information avaliable)\n"));    		
        	else
        		console.display(String.format("(No debug information found)\n"));  		    		
    	}
    	    	
    	Progress progress = engine.getProgress();
    	if (progress.getState() != Progress.State.IDLE) {
    		
    		console.display(String.format("Currently busy: %s (%d/%d)\n", 
    				progress.getDescription(), 
    				progress.getCurValue(), 
    				progress.getMaxValue()));    		
    	}
    	else {
	    	Route route = engine.getRoute();
	    	if (route == null) {
	    		console.display(String.format("No Paths/Functions registered\n"));    		
	    	} else {
	    		console.display("Loaded " + route.getStatusString() + "\n");
	    	}
    	}
    }    
}
