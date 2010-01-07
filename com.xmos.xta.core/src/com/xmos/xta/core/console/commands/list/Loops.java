package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.defines.LoopInfo;

public class Loops extends Command {	

	public Loops() {
	}
	
    public int numArgs() {
    	return 0;
    }        

	public ArgType[] getArgTypes() {
        return super.getArgTypes();            
	}    
    
    public String name() {
		return "loops";
    }        

    public String commandLine() {
   		return "loops";
    }
    
    public String description(Engine engine, boolean detailed) {
    	return "loops: Displays the loop defines";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) { 
			return null;
		}

    	return new Loops();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}
	    
		Collection<LoopInfo> infos = engine.getLoopDefines();
	    if (infos.isEmpty()) {
	    	console.display("none\n");
	    	return;
	    }
		for (LoopInfo info : infos) {
			console.display(info + "\n");
		}	    
    }
 }