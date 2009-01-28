package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.SrcDisplayer;
import com.xmos.xta.engine.Engine;

class Mix extends Command {
	private int m_pathId;
	private String m_fileName;

	Mix() {
		m_pathId = 0;
		m_fileName = "";
	}

	Mix(int pathId, String fileName) {
		m_pathId = pathId;
		m_fileName = fileName;
	}
	
    public int numArgs() {
    	return 2;
    }        

    public String name() {
    	return "mix";
    }        

    public String commandLine() {
    	return "mix " + m_pathId + " " + m_fileName;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "mix <path id> <source file>: Display annotated source file + assembly instructions for the given path id";      	
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
        String fileName = args[index + 2];    	
    	return new Mix(pathId, fileName);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;

		if (!isPathValid(engine, console, m_pathId))
			return;
		
		SrcDisplayer displayer = new SrcDisplayer(engine, console, m_pathId, m_fileName, true);
		displayer.display();
   }    
}
