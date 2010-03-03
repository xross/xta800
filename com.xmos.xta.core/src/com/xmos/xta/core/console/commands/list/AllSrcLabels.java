package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class AllSrcLabels extends Command {
	public AllSrcLabels() {
	}

    public int numArgs() {
    	return 0;
    }

    public String name() {
    	return "allsrclabels";
    }

    public String commandLine() {
    	return "allsrclabels";
    }

    public String description(Engine engine, boolean detailed) {
        return "allsrclabels: Lists all the possible locations for source labels";
    }

    public Command clone(String[] args, int index) {
        return new AllSrcLabels();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (NodeCorePair activeCore : engine.getActiveCores()) {			
			for (SrcLabelTableInfo info : engine.getSrcLabelTable(activeCore.getNode(), activeCore.getCore()).values()) {
	    	    console.display(String.format("node: %d, core %d, pc: 0x%x, compilation dir: %s, filename: %s, line numbers: %d - %d\n",
	    	    		activeCore.getNode(), activeCore.getCore(), 
	    	    		info.getPc(), info.getCompilationDir(), info.getFileName(),
	    	    		info.getLineNumberStart(), info.getLineNumberEnd()));
	    	}			
		}
    }
}
