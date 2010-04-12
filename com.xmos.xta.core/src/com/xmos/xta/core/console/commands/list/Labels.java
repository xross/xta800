package com.xmos.xta.core.console.commands.list;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.Conversion;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.CoreId;

public class Labels extends Command {
	private static final String[] COMMAND_NAMES = { "labels" };

	public Labels() {
		super(COMMAND_NAMES);
	}

	public int numArgs() {
        return 0;
	}

	public String commandLine() {
   		return name();
	}

	public String description(Engine engine, boolean detailed) {
        return name() + ": Lists the labels";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

   		return new Labels();
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

       	listLabels(engine, console);
	}

	private void listLabels(final Engine engine, final Console console) {
		for (CoreId activeCore : engine.getActiveCores()) {			
	    	Iterator<Map.Entry<String, Set<Long>>> iter = engine.getLabels(activeCore).entrySet().iterator();
	    	while (iter.hasNext()) {
	    	    Map.Entry<String, Set<Long>> pair = iter.next();
	    	    console.display(String.format("%s, label: %s, pcs: %s\n",
	    	    		activeCore.toString(), 
	    	    		pair.getKey(), Conversion.pcSetToString(pair.getValue())));
	    	}				
		}
	}
}
