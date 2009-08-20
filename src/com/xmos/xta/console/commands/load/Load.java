package com.xmos.xta.console.commands.load;

import java.io.File;
import java.util.Vector;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.ConsoleFileFinder;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandFactory;
import com.xmos.xta.console.commands.Invalid;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.SrcStringInfo;
import com.xmos.xta.sourcemapper.FileFinder;

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
		FileFinder fileFinder = new ConsoleFileFinder(getSrc(), engine);	
		if (engine.loadExecutable(getSrc(), new File(m_executableName), fileFinder)) {
		    runSectionCommands(shell, engine, console);
		}
    }

	public String toString() {
		return commandLine();
	}
	
    private void runSectionCommands(Shell shell, Engine engine, Console console) {
	    for (SrcStringInfo cmdInfo : engine.getSrcLevelCommands()) {
	    	String commandString = cmdInfo.getString();
            String[] tokens = commandString.trim().split("\\s+");
            
            CommandSource src = new CommandSource(cmdInfo.getFileName(), cmdInfo.getLineNumber());
            Command command = CommandFactory.buildCommand(src,
        		tokens, 0, new Vector<Command>(), console);
		
            if (command == null) {
            	command = new Invalid(commandString, 
            		    String.format("Invalid command in source file '%s'\n", commandString), src);
            }

		    command.execute(shell, engine, console);
	    }		
    }
}
