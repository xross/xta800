package com.xmos.xta.core.console.commands.status;

import java.io.File;
import java.util.List;

import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;

public class Status extends Command {
	public Status() {
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

    public Command clone(String[] args, int index) {
        return new Status();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {

    	console.display(String.format("TimingFailAsError: %s\n",
    			engine.getConfig().m_timingFailuresAsErrors ? "true" : "false"));
    	console.display(String.format("WarnAsError: %s\n",
    			engine.getConfig().m_warnAsErrors ? "true" : "false"));
    	
    	console.display(String.format("SrcPaths: %s\n", engine.getSrcPaths()));
    	console.display(String.format("Case: %s\n", engine.getCase().toString()));
    	console.display(String.format("Verbosity: %d\n", engine.getVerbosity()));
    	
    	File executable = engine.getExecutable();
    	if (executable != null) {
    		console.display(String.format("Loaded executable: %s\n", executable.getName()));
    	} else {
    		console.display(String.format("No executable loaded\n"));
    		return;
    	}

    	console.display(String.format("Current node: %d\n", engine.getNodeNumber()));    	
    	console.display(String.format("Current core: %d\n", engine.getCoreNumber()));

    	console.display(String.format("Frequency:"));
    	for (int nodeNumber : engine.getXeLoader().getNodeNumbers()) {
    		double mhz = (double)engine.getNodeHz(nodeNumber) / 1000000;
    		console.display(String.format(" Node %d, %.1f Mhz\n", nodeNumber, mhz));
    	}

    	for (MemoryImage memoryImage : engine.getMemoryImages()) {
    		int nodeNumber = memoryImage.getNodeNumber();
    		int coreNumber = memoryImage.getCoreNumber();
    		int threads = engine.getNumThreads(nodeNumber, coreNumber);
    		console.display(String.format("Node: %d, Core: %d, Threads: %d ",
    				nodeNumber, coreNumber, threads));

    		DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(nodeNumber, coreNumber);    		    		
    		if (debugInfo.getDebugEngine().hasDebugInfo())
    			console.display(String.format("(Debug information avaliable)\n"));    		
        	else
        		console.display(String.format("(No debug information found)\n"));  		    		
    	}
    	    	
    	if (engine.isBusy()) {    		
    		console.display("Currently busy...\n");
    		
    	} else {
    		List<Route> routes = engine.getRoutes();	    	
	    	if (engine.getRoutes().size() > 0) {
	    		for (Route route : routes) {
		    		console.display(String.format("Loaded Route(%d): %s %s\n", 
		    				route.getRouteId(),
		    				route.getRouteDescription().getDescription(),
		    				route.getEndpointsPcsString()));	    			
	    		}
	    		
	    	} else {
	    		console.display(String.format("No routes analysed\n"));    			    		
	    	}
    	}
    }    
}
