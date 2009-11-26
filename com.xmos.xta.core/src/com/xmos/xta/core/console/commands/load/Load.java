package com.xmos.xta.core.console.commands.load;

import java.io.File;
import java.util.Vector;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandFactory;
import com.xmos.xta.core.console.commands.Invalid;
import com.xmos.xta.core.engine.CommandRunner;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;

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
    
    public void execute(final Shell shell, final Engine engine, final Console console) {    	
		engine.loadExecutable(getSrc(), new File(m_executableName), new CommandRunner() {
			public void run(String command, CommandSource src, int commandNum) {
				runSectionCommand(shell, engine, console, command, src);
			}			
		});
    }

	public String toString() {
		return commandLine();
	}

    private void runSectionCommand(Shell shell, Engine engine, Console console, String commandStr, CommandSource src) {
        String[] tokens = commandStr.trim().split("\\s+");
            
        Command command = CommandFactory.buildCommand(src,
        		tokens, 0, new Vector<Command>(), console);
		
        if (command == null) {
          	command = new Invalid(commandStr, 
         		    String.format("invalid command in source file '%s'\n", commandStr), src);
        }

	    command.execute(shell, engine, console);
    }
}
