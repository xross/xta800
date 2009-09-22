package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class AllCalls extends Command {
	public AllCalls() {
	}

    public int numArgs() {
    	return 0;
    }        
	
    public String name() {
    	return "allcalls";
    }        

    public String commandLine() {
    	return "allcalls";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "allcalls: Lists all the possible locations for calls";      	
    }

    public Command clone(String[] args, int index) {
        return new AllCalls();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			
        
		if (engine.getCallTable().entrySet().size() == 0){
			console.display("none\n");
			return;
		}

		for (CodePointTableInfo info : engine.getCallTable().values()) {
    	    console.display(String.format("pc: 0x%x, compilation dir: %s, filename: %s, line number: %d\n", 
    	    		info.getPc(), info.getCompilationDir(), 
    	    		info.getFileName(), info.getLineNumber()));
    	}
    }

}
