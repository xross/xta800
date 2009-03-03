package com.xmos.xta.console.completors;

import java.util.List;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

import jline.Completor;

public class HelpCompletor implements Completor {
	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, List candidates) {			
        String start = (buffer == null) ? "" : buffer;
            
        // Checks for matching top level commands..
        for (Command command : CommandFactory.getCommands()) {
         	if (command.name().startsWith(start)) {
                candidates.add(command.name());
           	}
        }

 	    // Checks for matching reference options..
        for (ReferenceClass referenceClass : ReferenceClass.values()) {
            if (referenceClass.name().startsWith(start)) {
        	   	candidates.add(referenceClass.name());
        	}
        }
		return 0;
	}
}
