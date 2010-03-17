package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class SrcLabels extends Command {
	private static final String[] COMMAND_NAMES = { "srclabels" };

	public SrcLabels() {
		super(COMMAND_NAMES);
	}

	public int numArgs() {
        return 0;
	}

	public String commandLine() {
   		return name();
	}

	public String description(Engine engine, boolean detailed) {
        return name() + ": Lists the source labels";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

   		return new SrcLabels();
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

       	listSrcLabels(engine, console);
	}

	private void listSrcLabels(final Engine engine, final Console console) {
		for (NodeCorePair activeCore : engine.getActiveCores()) {			
			for (CodePointInfo info : engine.getSrcLabels(activeCore.getNode(), activeCore.getCore())) {
	    	    console.display(String.format("node: %d, core: %d, label: %s, pc: 0x%x", 
	    	    		activeCore.getNode(), activeCore.getCore(), info.getLabel(), info.getPc()));

	    	    if (!info.getCompilationDir().equals("")) {
	    	    	console.display(", compilation dir: " + info.getCompilationDir());
	    	    	console.display(", filename: " + info.getFileName());
	    	    	console.display(", line number: " + info.getLineNumber());
	    	    }
	    	    console.display("\n");
	    	}			
		}
	}
}
