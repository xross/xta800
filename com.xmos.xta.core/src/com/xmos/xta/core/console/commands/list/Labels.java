package com.xmos.xta.core.console.commands.list;

import java.util.Iterator;
import java.util.Map;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;

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
	
	private void listLabels(Engine engine, Console console) {
		if (engine.getLabels().entrySet().size() == 0){
			console.display("none\n");
			return;
		}
		
    	Iterator<Map.Entry<String, Long>> iter = engine.getLabels().entrySet().iterator();        
    	while (iter.hasNext()) {
    	    Map.Entry<String, Long> pair = iter.next();
    	    console.display(String.format("Label: %s, pc: 0x%x\n", pair.getKey(), pair.getValue()));
    	}		
	}
}
