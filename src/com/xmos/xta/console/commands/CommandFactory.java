package com.xmos.xta.console.commands;

import java.util.Vector;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.commands.Add.Add;
import com.xmos.xta.console.commands.Analyse.Analyse;
import com.xmos.xta.console.commands.Clear.Clear;
import com.xmos.xta.console.commands.Config.Config;
import com.xmos.xta.console.commands.Echo.Echo;
import com.xmos.xta.console.commands.Exit.Exit;
import com.xmos.xta.console.commands.Help.Help;
import com.xmos.xta.console.commands.History.History;
import com.xmos.xta.console.commands.List.List;
import com.xmos.xta.console.commands.Load.Load;
import com.xmos.xta.console.commands.Print.Print;
import com.xmos.xta.console.commands.Pwd.Pwd;
import com.xmos.xta.console.commands.Remove.Remove;
import com.xmos.xta.console.commands.Set.Set;
import com.xmos.xta.console.commands.Source.Source;
import com.xmos.xta.console.commands.Status.Status;
import com.xmos.xta.console.commands.Unset.Unset;
import com.xmos.xta.console.commands.Version.Version;

public class CommandFactory {
	static private Command[] m_commands = { 
		new Add(),
		new Analyse(), 
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
