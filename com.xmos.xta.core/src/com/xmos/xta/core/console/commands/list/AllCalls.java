package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.utils.CoreId;

public class AllCalls extends Command {
	private static final String[] COMMAND_NAMES = { "allcalls" };

	public AllCalls() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Lists all the possible locations for calls";
    }

    public Command clone(String[] args, int index) {
        return new AllCalls();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (CoreId activeCore : engine.getActiveCores()) {
			for (CodePointTableInfo info : engine.getCallTable(activeCore).values()) {
	    	    console.display(String.format("%s, pc: 0x%x, compilation dir: %s, filename: %s, line number: %d\n",
	    	    		activeCore.toString(), 
	    	    		info.getPc(), info.getCompilationDir(),
	    	    		info.getFileName(), info.getLineNumber()));
	    	}
		}		
    }

}
