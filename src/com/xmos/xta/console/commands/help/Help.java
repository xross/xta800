package com.xmos.xta.console.commands.help;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandFactory;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.types.ReferenceType;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class Help extends Command {
	private Command m_command;
	private ReferenceClass m_referenceClass;
	
	public Help() {
		m_command = null;
		m_referenceClass = null;
	}

	public Help(Command command) {
		m_command = command;
	}
	
	public Help(ReferenceClass referenceClass) {
		m_referenceClass = referenceClass;
	}
	
    public int numArgs() {
    	if (m_command != null) {
    		return 1;
    	}
    	if (m_referenceClass != null) {
    	    return 1;
    	} 
    	return 0;
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.HELP};
	}    
    
    public String name() {
    	return "help";
    }        

    public String commandLine() {
    	if (m_command != null) {
        	return "help " + m_command.name();
    	}
    	if (m_referenceClass != null) {
        	return "help " + m_referenceClass.name();
    	}
   		return "help";
    }
   
    public String description(Engine engine, boolean detailed) {
        String desc = "help [command/option]: Displays help message for the given command/option";  
        
        if (detailed) {
            desc += "\n      options: ";                          
            for (ReferenceClass referenceClass : ReferenceClass.values()) {
        	    desc += String.format("%s, ", referenceClass.name()); 
            }
            desc = desc.substring(0, desc.length() - 2);
        }
        return desc;
    }
    
    public Command clone(String[] args, int index) {
		if (areArgsDone(args, index + 1)) {
			return new Help();
		}
    	
		String arg = args[index + 1];    	
		// Checks for matching commands..
    	Command[] avaliableCommands = CommandFactory.getCommands();
    	for (Command command : avaliableCommands) {
    		if (command.name().equals(arg)) {
    			return new Help(command);
    		}
    	}

		// Checks for matching options..
        for (ReferenceClass referenceClass : ReferenceClass.values()) {
    	    if (referenceClass.name().equals(arg)) {
    	    	return new Help(referenceClass);
    	    }
        }
        
        // Error..
    	return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) { 
    	if (m_command != null) {
    		// Displays help for a specific command.
    		console.display(String.format("  [-]%s\n", m_command.description(engine, true)));    		
    	} else if (m_referenceClass != null) {
    		// Displays help for a specific option.
    		console.display("  Any of the following are permitted:\n");
    		
    		ReferenceType[] referenceTypes = ReferenceTypeFactory.getReferenceClass(m_referenceClass); 
    		for (ReferenceType referenceType : referenceTypes) {
    			console.display(String.format("    %s: %s\n", 
    					referenceType.name(), referenceType.description())); 
    		}    		
    	} else {    	
            // Displays help for all commands.    	
	    	Command[] avaliableCommands = CommandFactory.getCommands();
	    	for (Command command : avaliableCommands) {
	    		console.display(String.format("  [-]%s\n", command.description(engine, false)));   		
	    	}
    	}
    }    
}
