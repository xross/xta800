package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.SrcDisplayer;
import com.xmos.xta.engine.Engine;

class Mix extends Command {
	private int m_pathId;

	Mix() {
		m_pathId = 0;
	}

	Mix(int pathId) {
		m_pathId = pathId;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "mix";
    }        

    public String commandLine() {
    	return "mix " + m_pathId;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "mix <path id>: Display annotated source file(s) + assembly instructions for the given path id";      	
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
    	return new Mix(pathId);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;

		if (!isPathValid(engine, console, m_pathId))
			return;
		
		SrcDisplayer displayer = new SrcDisplayer(engine, console, m_pathId, true);
		displayer.display();
   }    
}
