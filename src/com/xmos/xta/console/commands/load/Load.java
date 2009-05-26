package com.xmos.xta.console.commands.load;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.ConsoleFileFinder;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandFactory;
import com.xmos.xta.console.commands.Invalid;
import com.xmos.xta.engine.Engine;

public class Load extends Command {
	private String m_executableName;
	
	public Load() {
		m_executableName = "";
	}
	
	public Load(String executableName) {
		m_executableName = executableName;
	}
	
    public int numArgs() {
    	return 1;
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.FILE};
	}	
    
    public String name() {
    	return "load";
    }        

    public String commandLine() {
    	return "load " + m_executableName;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "load <xe file>: Loads the given xmos executable file";      	
    }
    
    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	String executableName = args[index + 1];
        return new Load(executableName);        
    }
    
    public void execute(Shell shell, Engine engine, Console console) {    	
    	File file = new File(new File(m_executableName).getAbsolutePath());
    	try {
			file = file.getCanonicalFile();
		} catch (IOException e) {
			console.display(String.format("Error: Failed to resolve executable path: %s\n", m_executableName));
		}
		ConsoleFileFinder fileFinder = new ConsoleFileFinder(engine, console);	
		if (engine.loadExecutable(file, fileFinder)) {
		    runSectionCommands(shell, engine, console);
		}
    }

	public String toString() {
		return commandLine();
	}
	
    private void runSectionCommands(Shell shell, Engine engine, Console console) {
	    for (String commandString : engine.getSrcLevelCommands()) {
            String[] tokens = commandString.trim().split("\\s+");
            Command command = CommandFactory.buildCommand(
        		tokens, 0, new Vector<Command>(), console);
		
            if (command == null)
            	command = new Invalid(commandString, 
            		    String.format("Invalid command in source file '%s'\n", commandString));
            	
		    command.execute(shell, engine, console);
		    shell.addToCommandHistory(command);
	    }		
    }
}
