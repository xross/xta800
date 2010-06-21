package com.xmos.xta.core.console.commands.list;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;

public class Sources extends Command {
	private static final String[] COMMAND_NAMES = { "sources" };

	public Sources() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Lists the source files";
    }

    public Command clone(String[] args, int index) {
        return new Sources();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (SourceFile sourceFile : engine.getSourceMapper().getSourceFiles()) {
			DebugFile debugFile = sourceFile.getDebugFile();
			
	    	console.display(String.format("compilation dir: %s, filename: %s",
	    			debugFile.getBaseDir(), 
	    			debugFile.getFileName()));

  	    	if (debugFile.getFile() == null) {
  	    		console.display(" (File not found)");
  	    	}
  	    	console.display("\n");			
		}
    }
}
