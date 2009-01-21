package com.xmos.xta.console.commands;

import java.util.Vector;

public class CommandFactory {
	static private Command[] m_commands = { 
		new Load(), 
		new Analyse(), 
		new List(),
		new Status(),
		new Summary(), 
		new Print(), 
		new Set(),
		new Assert(),
		new Add(),
		new Remove(),
		new Source(),
		new History(),
		new Pwd(),
		new Config(),
		new Clear(),
		new Help(), 
		new Exit(),	
	    new Echo(),
	};
	
	public static Command[] getCommands() {
		return m_commands;
	}
	
    public static Command buildCommand(String[] args, int index, 
    		Vector<Command> commandHistory, boolean commandLineMode) {
    	String arg = args[index];    	
        
        if (isPingCommand(arg)) {
        	return getPingCommand(arg, commandHistory);
        }    	
    	
    	arg = stripLeadingDash(arg);
    	for (Command command : m_commands) {
    		if (arg.equals(command.name())) {
    			return command.clone(args, index, commandLineMode);
    		}    			
    	}
    	return null;
    }
    
    private static String stripLeadingDash(String arg) {
    	String newArg = arg;
    	if (arg.charAt(0) == '-')
    		newArg = arg.substring(1);
    	
    	return newArg;
    }
    
    private static boolean isPingCommand(String arg) {
        if ((arg.charAt(0) == '!'))
        	return true;
        
        return false;
    }

    private static Command getPingCommand(String arg, Vector<Command> commandHistory) {
    	arg = arg.substring(1);
    	
        int commandIndex;
        try {
        	commandIndex = Integer.parseInt(arg);
        }
        catch (NumberFormatException e) {
            return null;
        }    	        
    	
        if ((commandIndex < 0) || (commandIndex >= commandHistory.size()))            	
  		    return null;
        
        return commandHistory.get(commandIndex);    	
    }
}
