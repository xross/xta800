package com.xmos.xta.console.commands.List;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.TableInfo;

public class AllLabels extends Command {
	public AllLabels() {
	}

    public int numArgs() {
    	return 0;
    }        
	
    public String name() {
    	return "alllabels";
    }        

    public String commandLine() {
    	return "alllabels";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "alllabels: Lists all the possible locations for labels";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        return new AllLabels();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			
        
		if (engine.getLabelTable().entrySet().size() == 0){
			console.display("none\n");
			return;
		}

		for (TableInfo info : engine.getLabelTable().values()) {
    	    console.display(String.format("pc: 0x%x, compilation dir: %s, filename: %s, line number: %d\n", 
    	    		info.getPc(), info.getCompilationDir(), 
    	    		info.getFileName(), info.getLineNumber()));
    	}		        
    }
}
