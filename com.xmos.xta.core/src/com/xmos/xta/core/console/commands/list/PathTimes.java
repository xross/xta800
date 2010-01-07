package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.defines.PathTimeInfo;

public class PathTimes extends Command {

	public PathTimes() {
	}
	
    public int numArgs() {
    	return 0;    	
    }        

	public ArgType[] getArgTypes() {
        return super.getArgTypes();            
	}    

    public String name() {
		return "pathtimes";
    }        

    public String commandLine() {
   		return "pathtimes";
    }
    
    public String description(Engine engine, boolean detailed) {
    	return "pathtimes: Displays the path time defines";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) { 
			return null;
		}
		
    	return new PathTimes();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		Collection<PathTimeInfo> infos = engine.getPathTimeDefines();
	    if (infos.isEmpty()) {
	    	console.display("none\n");
	    	return;
	    }
		for (PathTimeInfo info : infos) {
			console.display(info + "\n");
		}	    
    }    
 }
