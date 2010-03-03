package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.SrcLevelLoopInfo;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class SrcLoops extends Command {
	public SrcLoops() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
  		return "srcloops";
	}

	public String commandLine() {
   		return "srcloops";
	}

	public String description(Engine engine, boolean detailed) {
        return "srcloops : Displays the loop counts embedded in the loaded executable";
	}

	public Command clone(String[] args, int index) {
   		return new SrcLoops();
	}

	public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (NodeCorePair activeCore : engine.getActiveCores()) {			
			for (SrcLevelLoopInfo srcLevelLoopInfo : engine.getSrcLevelLoops(activeCore.getNode(), activeCore.getCore())) {
	    	    console.display(String.format("node: %d, core %d, 0x%x: %d\n",
	    	    		activeCore.getNode(), activeCore.getCore(), 
	                    srcLevelLoopInfo.getPc(),
	                    srcLevelLoopInfo.getNumIterations()));
	    	}				
		}
	}
}
