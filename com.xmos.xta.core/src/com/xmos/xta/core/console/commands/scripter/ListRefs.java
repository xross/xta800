package com.xmos.xta.core.console.commands.scripter;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.scripter.Scripter;
import com.xmos.xta.core.engine.scripter.ScripterReference;

public class ListRefs extends Command {
	private static final String[] COMMAND_NAMES = { "listrefs" };

	public ListRefs() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": List all references which will be used in the script";
    }

    public Command clone(String[] args, int index) {
        return new ListRefs();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Scripter scripter = engine.getScripter();
		scripter.createRefs();
		Collection<ScripterReference> refs = scripter.getRefs();

		console.display(String.format("%-10s %-10s %-10s -> %10s\n",
				"Modifiable", "Enabled", "Reference", "MapsTo"));

		for (ScripterReference ref : refs) {
			String refString = ref.getBaseReference().getRef();
			console.display(String.format("%-10s %-10s %-10s -> %10s\n",
					ref.isModifiable(), ref.isEnabled(), refString, ref.getMapsToLabel()));
		}
    }
}

