package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.SrcLevelLoopInfo;
import com.xmos.xta.core.engine.utils.CoreId;

public class SrcLoops extends Command {
	private static final String[] COMMAND_NAMES = { "srcloops" };

	public SrcLoops() {
		super(COMMAND_NAMES);
	}

	public int numArgs() {
        return 0;
	}

	public String commandLine() {
   		return name();
	}

	public String description(Engine engine, boolean detailed) {
        return name() + ": Displays the loop counts embedded in the loaded executable";
	}

	public Command clone(String[] args, int index) {
   		return new SrcLoops();
	}

	public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (CoreId activeCore : engine.getActiveCores()) {			
			for (SrcLevelLoopInfo srcLevelLoopInfo : engine.getSrcLevelLoops(activeCore)) {
	    	    console.display(String.format("%s, 0x%x: %d\n",
	    	    		activeCore.toString(), 
	                    srcLevelLoopInfo.getPc(),
	                    srcLevelLoopInfo.getNumIterations()));
	    	}				
		}
	}
}
