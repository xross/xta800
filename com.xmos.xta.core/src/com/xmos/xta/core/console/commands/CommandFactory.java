package com.xmos.xta.core.console.commands;

import java.util.Vector;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.commands.Command.RedirectMode;
import com.xmos.xta.core.console.commands.add.Add;
import com.xmos.xta.core.console.commands.analyze.Analyze;
import com.xmos.xta.core.console.commands.clear.Clear;
import com.xmos.xta.core.console.commands.config.Config;
import com.xmos.xta.core.console.commands.debug.Debug;
import com.xmos.xta.core.console.commands.echo.Echo;
import com.xmos.xta.core.console.commands.exit.Exit;
import com.xmos.xta.core.console.commands.help.Help;
import com.xmos.xta.core.console.commands.history.History;
import com.xmos.xta.core.console.commands.list.List;
import com.xmos.xta.core.console.commands.load.Load;
import com.xmos.xta.core.console.commands.print.Print;
import com.xmos.xta.core.console.commands.pwd.Pwd;
import com.xmos.xta.core.console.commands.remove.Remove;
import com.xmos.xta.core.console.commands.scripter.Scripter;
import com.xmos.xta.core.console.commands.set.Set;
import com.xmos.xta.core.console.commands.source.Source;
import com.xmos.xta.core.console.commands.status.Status;
import com.xmos.xta.core.console.commands.version.Version;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;

public class CommandFactory {
	static private Command[] m_commands = {
		new Add(),
		new Analyze(),
		new Config(),
		new Clear(),
		new Debug(),
	    new Echo(),
		new Exit(),
		new Help(),
		new History(),
		new Load(),
		new List(),
		new Print(),
		new Pwd(),
		new Remove(),
		new Scripter(),
		new Set(),
		new Source(),
		new Status(),
	    new Version(),
	};

	public static Command[] getCommands() {
		return m_commands;
	}

    public static Command buildCommand(CommandSource src, String[] args, int index,
    		Vector<Command> commandHistory, Console console) {
    	String arg = args[index];
    	
    	
        if (isPlingCommand(arg)) {
        	Command command = getPlingCommand(arg, commandHistory);
        	if (command != null) {
				command.setSrc(src);
				return command;
			}
        }

    	arg = stripLeadingDash(arg);
    	for (Command command : m_commands) {
    		if (command.matches(arg)) {
    			Command cloned = command.clone(args, index);
    			if (cloned != null) {
        			cloned.setSrc(src);
        			if (!processFileRedirect(cloned, args, index)) {
        				return null;
        			}
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

    private static Command getPlingCommand(String arg, Vector<Command> commandHistory) {
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

    private static boolean processFileRedirect(Command command, String[] args, int index) {
    	int nextIndex = index + command.numArgs() + 1;
    	if (nextIndex < args.length) {
    	    if (args[nextIndex].equals(Command.REDIRECT_OVERWITE) ||
    	    	args[nextIndex].equals(Command.REDIRECT_APPEND)) {

    	    	// Check that the filename is specified..
    	    	if ((nextIndex + 1) >= args.length)
    	    		return false;

    	    	command.setFileRedirect(args[nextIndex + 1]);
    	    	if (args[nextIndex].equals(">")) {
    	    		command.setFileRedirectMode(RedirectMode.OVERWRITE);
    	    	} else {
    	    		command.setFileRedirectMode(RedirectMode.APPEND);
    	    	}
    	    }
    	}
    	return true;
    }
}
