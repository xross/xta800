package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.displayers.AsmDisplayer;
import com.xmos.xta.engine.Engine;

class Asm extends Command {
	int m_pathId;

	Asm() {
		m_pathId = 0;
	}

	Asm(int pathId) {
		m_pathId = pathId;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "asm";
    }        

    public String commandLine() {
    	return "asm " + m_pathId;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "asm <path id>: Display annotated assembly for given path id";      	
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
    	return new Asm(pathId);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
    	
        engine.displayRoute(new AsmDisplayer(console, m_pathId, 
        		engine.getXeLoader().getArchitecture(), engine.getXeObject()));	
   }
    
}
