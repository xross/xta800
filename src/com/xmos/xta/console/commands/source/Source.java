package com.xmos.xta.console.commands.source;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandFactory;
import com.xmos.xta.console.commands.Invalid;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;

public class Source extends Command {
	private String m_fileName;
	
	public Source() {
		m_fileName = "";
	}
	
	public Source(String fileName) {
		m_fileName = fileName;
	}
	
    public int numArgs() {
    	return 1;
    }        

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.FILE};
	}    
    
    public String name() {
    	return "source";
    }        

    public String commandLine() {
    	return "source " + m_fileName;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "source <file name>: Sources the given command file";      	
    }
    
    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	String fileName = args[index + 1];
        return new Source(fileName);        
    }
    
    public void execute(Shell shell, Engine engine, Console console) {    	
        try {
            BufferedReader fp = new BufferedReader(new FileReader(m_fileName));
            int location = shell.getLocation(this);            
            try {
            	int lineNumber = 1;
                String line;
                while ((line = fp.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0) {            
                        String[] tokens = line.trim().split("\\s+");
                        
                    	if (!tokens[0].startsWith("#")) {
                    		CommandSource src = new CommandSource(m_fileName, lineNumber);
                            Command command = CommandFactory.buildCommand(src,
                            		tokens, 0, shell.getCommandHistory(), console);
                            
                            boolean invalidCommand = false;
                            if (command != null) {
                        		invalidCommand = (command.numArgs() != (tokens.length - 1));
                                if (!invalidCommand) {	
                            	    shell.addCommand(command, location + 1);
                            	    location++;
                                }
                            } else {
                            	invalidCommand = true;
                            }

                            if (invalidCommand) {
                            	String errorMessage = String.format("%s:%d: error: invalid command\n", m_fileName, lineNumber);
                            	shell.addCommand(new Invalid(line, errorMessage), location+1);
                            	location++;
                            }
                    	}
                    }
                    ++lineNumber;
                }
                fp.close();
            }
            catch (IOException e) {
       			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
       					"problem reading the file: %s\n", m_fileName)); 
            }                               
        }
        catch (IOException e) {
   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
   					"unable to open file for reading: %s\n", m_fileName)); 
        }    	
    }        
}
