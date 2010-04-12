package com.xmos.xta.core.console.commands.debug;

import java.util.Map;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreId;

public class ToPoints extends Command {
	private static final String[] COMMAND_NAMES = { "topoints" };
	
	public ToPoints() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Displays the to endpoints currently configured";
    }

    public Command clone(String[] args, int index) {
    	return new ToPoints();
    }

    public void execute(Shell shell, Engine engine, Console console) {
    	Map<CoreId, Reference> tos = engine.getPathPoints().getTos();    	
    	for (CoreId core : tos.keySet()) {
    		console.display(tos.get(core).toString() + "\n");    		
    	}
    }
}
