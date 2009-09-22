package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class AllEndpoints extends Command {
	public AllEndpoints() {
	}

    public int numArgs() {
    	return 0;
    }        
	
    public String name() {
    	return "allendpoints";
    }        

    public String commandLine() {
    	return "allendpoints";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "allendpoints: Lists all the possible locations for endpoints";      	
    }

    public Command clone(String[] args, int index) {
        return new AllEndpoints();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			
        
		if (engine.getEndpointTable().entrySet().size() == 0){
			console.display("none\n");
			return;
		}

		for (CodePointTableInfo info : engine.getEndpointTable().values()) {
    	    console.display(String.format("pc: 0x%x, compilation dir: %s, filename: %s, line number: %d\n", 
    	    		info.getPc(), info.getCompilationDir(), 
    	    		info.getFileName(), info.getLineNumber()));
    	}
    }
}
