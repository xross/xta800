package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Help extends Command {
	private Command m_command;
	
	Help() {
		m_command = null;
	}

	Help(Command command) {
		m_command = command;
	}
	
    public int numArgs() {
    	if (m_command == null) {
    	    return 0;
    	}
    	return 1;
    }        

    public String name() {
    	return "help";
    }        

    public String commandLine() {
    	if (m_command == null) {
    	    return "help";
    	}
    	return "help " + m_command.name();
    }
   
    public String description(Engine engine, boolean detailed) {
        return "help [command]: Displays help message for the given command";      	
    }
    
    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + 1) >= args.length) {
			return new Help();
		}
    	
		String arg = args[index + 1];    	
		if (commandLineMode) {
			// If we are processing command from the command line, then we only
			// accepts the default help (i.e. not the help 'command' option).
			return new Help();    	
    	}
    	else {
    		// When we are in the shell, then we can accept
    		// the more detailed help 'command' option.
	    	Command[] avaliableCommands = CommandFactory.getCommands();
	    	for (Command command : avaliableCommands) {
	    		if (command.name().equals(arg)) {
	    			return new Help(command);
	    		}
	    	}
	    	return null;
    	}    		    	
    }
    
    public void execute(Shell shell, Engine engine, Console console) {    	
    	if (m_command == null) {
            // Displays help for all commands.    	
	    	Command[] avaliableCommands = CommandFactory.getCommands();
	    	for (Command command : avaliableCommands) {
	    		console.display(String.format("  [-]%s\n", command.description(engine, false)));   		
	    	}
    	}
    	else {
    		// Displays help for a specific command.
    		console.display(String.format("  [-]%s\n", m_command.description(engine, true))); 
    	}
    }    
}
