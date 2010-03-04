package com.xmos.xta.core.console.commands.list;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.Conversion;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class Labels extends Command {
	public Labels() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
  		return "labels";
	}

	public String commandLine() {
   		return "labels";
	}

	public String description(Engine engine, boolean detailed) {
        return "labels: Lists the labels";
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
		for (NodeCorePair activeCore : engine.getActiveCores()) {			
	    	Iterator<Map.Entry<String, Set<Long>>> iter = engine.getLabels(activeCore.getNode(), activeCore.getCore()).entrySet().iterator();
	    	while (iter.hasNext()) {
	    	    Map.Entry<String, Set<Long>> pair = iter.next();
	    	    console.display(String.format("Node: %d, Core: %d, label: %s, pcs: %s\n",
	    	    		activeCore.getNode(), activeCore.getCore(), 
	    	    		pair.getKey(), Conversion.pcSetToString(pair.getValue())));
	    	}				
		}
	}
}
