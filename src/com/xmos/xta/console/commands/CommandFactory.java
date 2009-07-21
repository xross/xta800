package com.xmos.xta.console.commands;

import java.util.Vector;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.commands.add.Add;
import com.xmos.xta.console.commands.analyse.Analyse;
import com.xmos.xta.console.commands.clear.Clear;
import com.xmos.xta.console.commands.config.Config;
import com.xmos.xta.console.commands.echo.Echo;
import com.xmos.xta.console.commands.exit.Exit;
import com.xmos.xta.console.commands.help.Help;
import com.xmos.xta.console.commands.history.History;
import com.xmos.xta.console.commands.list.List;
import com.xmos.xta.console.commands.load.Load;
import com.xmos.xta.console.commands.print.Print;
import com.xmos.xta.console.commands.pwd.Pwd;
import com.xmos.xta.console.commands.remove.Remove;
import com.xmos.xta.console.commands.set.Set;
import com.xmos.xta.console.commands.source.Source;
import com.xmos.xta.console.commands.status.Status;
import com.xmos.xta.console.commands.version.Version;

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
	    new Version(),
	};
	
	public static Command[] getCommands() {
		return m_commands;
	}
	
    public static Command buildCommand(String[] args, int index, 
    		Vector<Command> commandHistory, Console console) {
    	String arg = args[index];    	
        
        if (isPlingCommand(arg)) {
        	return getPingCommand(arg, commandHistory);
        }
    	
    	arg = stripLeadingDash(arg);
    	for (Command command : m_commands) {
    		if (arg.equals(command.name())) {
    			Command cloned = command.clone(args, index);    			
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
    
    public static boolean isPlingCommand(String arg) {
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
