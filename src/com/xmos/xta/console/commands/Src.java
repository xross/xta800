package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.SrcDisplayer;
import com.xmos.xta.engine.Engine;

class Src extends Command {
	private int m_pathId;

	Src() {
		m_pathId = 0;
	}

	Src(int pathId) {
		m_pathId = pathId;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "src";
    }        

    public String commandLine() {
    	return "src " + m_pathId;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "src <path id>: Display annotated source file(s) for given path id";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int pathId;
    	try {
    	    pathId = Integer.parseInt(args[index + 1]);
    	}
    	catch (NumberFormatException e) {
    	    return null;
    	}
    	return new Src(pathId);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;

		if (!isPathValid(engine, console, m_pathId))
			return;
				
		SrcDisplayer displayer = new SrcDisplayer(engine, console, m_pathId, false);
		displayer.display();	
   }    
}
