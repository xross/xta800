package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class AllEndpoints extends Command {
	private static final String[] COMMAND_NAMES = { "allendpoints" };

	public AllEndpoints() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Lists all the possible locations for endpoints";
    }

    public Command clone(String[] args, int index) {
        return new AllEndpoints();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (NodeCorePair activeCore : engine.getActiveCores()) {			
			for (CodePointTableInfo info : engine.getEndpointTable(activeCore.getNode(), activeCore.getCore()).values()) {
		   	    console.display(String.format("node: %d, core: %d, pc: 0x%x, compilation dir: %s, filename: %s, line number: %d\n",
		   	    		activeCore.getNode(), activeCore.getCore(), 
		   	    		info.getPc(), info.getCompilationDir(),
		   	    		info.getFileName(), info.getLineNumber()));
		   	}			
		}
    }
}
