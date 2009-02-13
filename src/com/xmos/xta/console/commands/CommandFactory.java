package com.xmos.xta.console.commands;

import java.util.Vector;

import com.xmos.xta.console.Console;

public class CommandFactory {
	static private Command[] m_commands = { 
		new Add(),
		new Analyse(), 
		new Assert(),
		new Config(),
		new Clear(),
	    new Echo(),
		new Exit(),	
		new Help(), 
		new History(),
		new Load(), 
		new List(),
		new Print(), 
		new Pwd(),
		new Remove(),
		new Set(),
		new Source(),
		new Status(),
		new Summary(), 
		new Unset(),
	    new Version(),
	};
	
	public static Command[] getCommands() {
		return m_commands;
	}
	
    public static Command buildCommand(String[] args, int index, 
    		Vector<Command> commandHistory, Console console, boolean commandLineMode) {
    	String arg = args[index];    	
        
        if (isPingCommand(arg)) {
        	return getPingCommand(arg, commandHistory);
        }
    	
    	arg = stripLeadingDash(arg);
    	for (Command command : m_commands) {
    		if (arg.equals(command.name())) {
    			Command cloned = command.clone(args, index, commandLineMode);    			
    			if (cloned == null) {
    		    	console.display(
    		    			"Invalid argument(s) to '" + command.name() + 
    		    			"' command (Try 'help " + command.name() + "'..)\n");    	    				
    			}
    			return cloned;
    		}
    	}
    	console.display("Invalid command (Try 'help'..)\n");    	
    	return null;
    }
    
    private static String stripLeadingDash(String arg) {
    	String newArg = arg;
    	if (arg.charAt(0) == '-')
    		newArg = arg.substring(1);
    	
    	return newArg;
    }
    
    public static boolean isPingCommand(String arg) {
        if ((arg.charAt(0) == '!'))
        	return true;
        
        return false;
    }

    private static Command getPingCommand(String arg, Vector<Command> commandHistory) {
    	arg = arg.substring(1);
    	
        int commandIndex;
        try {
        	// Need to subtract 1 since command 1 is in array position 0
        	commandIndex = Integer.parseInt(arg) - 1;
        }
        catch (NumberFormatException e) {
            return null;
        }
    	
        if ((commandIndex < 0) || (commandIndex >= commandHistory.size()))            	
  		    return null;
        
        return commandHistory.get(commandIndex);    	
    }
}
