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
	private static final String[] COMMAND_NAMES = { "status" };

	public Status() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Display current status";
    }

    public Command clone(String[] args, int index) {
        return new Status();
    }

    public void execute(Shell shell, Engine engine, Console console) {

    	console.display(String.format("TimingFailAsError: %s\n",
    			engine.getConfig().m_timingFailuresAsErrors ? "true" : "false"));
    	console.display(String.format("WarnAsError: %s\n",
    			engine.getConfig().getWarningsAsErrors() ? "true" : "false"));
    	console.display(String.format("ErrorAsWarn: %s\n",
    			engine.getConfig().getErrorsAsWarnings() ? "true" : "false"));

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

    	console.display(String.format("Frequency:"));
    	for (int nodeNumber : engine.getXeLoader().getNodeNumbers()) {
    		double mhz = (double)engine.getFrequency(nodeNumber);
    		console.display(String.format(" Node %d, %.1f MHz\n", nodeNumber, mhz));
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
		    				route.getRouteCoreContainer().getRouteContainer().getDescription(),
		    				route.getEndpointsPcsString()));
	    		}

	    	} else {
	    		console.display(String.format("No routes analyzed\n"));
	    	}
    	}

    	String fromEndPointName = engine.getPathPoints().getFromName();
    	if (!fromEndPointName.equals("")) {
   		    console.display(String.format("From endpoint '%s'\n", fromEndPointName));  
    	}
    	
    	String toEndPointName = engine.getPathPoints().getToName();
    	if (!toEndPointName.equals("")) {
    		console.display(String.format("To endpoint '%s'\n", toEndPointName));
    	}
    }
}
