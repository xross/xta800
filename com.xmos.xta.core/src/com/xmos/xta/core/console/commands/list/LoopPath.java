package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.defines.LoopPathInfo;

public class LoopPath extends Command {

	public LoopPath() {
	}

    public int numArgs() {
    	return 0;
    }

	public ArgType[] getArgTypes() {
        return super.getArgTypes();
	}

    public String name() {
		return "looppaths";
    }

    public String commandLine() {
   		return "looppaths";
    }

    public String description(Engine engine, boolean detailed) {
    	return "looppaths: Displays the loop path defines";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

    	return new LoopPath();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		Collection<LoopPathInfo> infos = engine.getLoopPathDefines();
	    if (infos.isEmpty()) {
	    	console.display("none\n");
	    	return;
	    }
		for (LoopPathInfo info : infos) {
			console.display(info + "\n");
		}
    }
}
