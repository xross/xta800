package com.xmos.xta.core.console.commands.list;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;

public class Exclusions extends Command {
	private static final String[] COMMAND_NAMES = { "exclusions" };

	public Exclusions() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

	public ArgType[] getArgTypes() {
        return super.getArgTypes();
	}

    public String commandLine() {
   		return name();
    }

    public String description(Engine engine, boolean detailed) {
    	return name() + ": Displays the exclusions";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

    	return new Exclusions();
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		listExclusions(engine, console);
    }

    private void listExclusions(Engine engine, Console console) {
    	Collection<Reference> exclusions = engine.getExclusions();
        if (exclusions == null)
	    	return;

	    if (exclusions.size() > 0) {
	    	for (Reference exclusion : exclusions) {
	    	    console.display(String.format("%s: %s\n",
	    	    		exclusion, exclusion.getPcString())); 
	    	}
	    } else {
	    	console.display("none\n");
	    }
    }
 }
