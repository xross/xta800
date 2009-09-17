package com.xmos.xta.console.commands.list;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.sourcemapper.SourceFile;

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
		
		for (SourceFile sourceFile : engine.getSourceMapper().getSourceFiles()) {
    	    console.display(String.format("compilation dir: %s, filename: %s\n", 
    	    		sourceFile.getBaseDir(), sourceFile.getFileName()));
			
		}
    }
}
