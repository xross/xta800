package com.xmos.xta.core.console.commands.list;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.SrcStringInfo;

public class SrcCommands extends Command {
	public SrcCommands() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
  		return "srccommands";
	}

	public String commandLine() {
   		return "srccommands";
	}

	public String description(Engine engine, boolean detailed) {
        return "srccommands : Displays the command list embedded in the loaded executable";
	}

	public Command clone(String[] args, int index) {
   		return new SrcCommands();
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		if (engine.getSrcLevelCommands().size() == 0){
			console.display("none\n");
			return;
		}

		for (SrcStringInfo cmdInfo : engine.getSrcLevelCommands()) {
    	    console.display(String.format("%s\n", cmdInfo.getString()));
    	}
	}
}
