package com.xmos.xta.core.console.commands.debug;

import java.util.Map;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;

public class FromPoints extends Command {
	private static final String[] COMMAND_NAMES = { "frompoints" };
	
	public FromPoints() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Displays the from endpoints currently configured";
    }

    public Command clone(String[] args, int index) {
    	return new FromPoints();
    }

    public void execute(Shell shell, Engine engine, Console console) {
    	Map<CoreId, Reference> froms = engine.getPathPoints().getFroms();    	
    	for (CoreId core : froms.keySet()) {
    		console.display(froms.get(core).toString() + "\n");    		
    	}
    }
}
