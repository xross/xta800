package com.xmos.xta.console.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Source extends Command {
	private String m_fileName;
	
	Source() {
		m_fileName = "";
	}
	
	Source(String fileName) {
		m_fileName = fileName;
	}
	
    public int numArgs() {
    	return 1;
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
    
    public Command clone(String[] args, int index, boolean commandLineMode) {
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
                            Command command = CommandFactory.buildCommand(
                            		tokens, 0, shell.getCommandHistory(), console, false);
                            
                            if (command != null) {
                            	shell.addCommand(command, location+1);
                            	location++;
                            } else {
                            	shell.addCommand(new Invalid(line), location+1);
                            	location++;
                            	console.display(String.format("Invalid command in file: %s, line number: %d\n",
                            		    m_fileName, lineNumber));                            	
                            }
                    	}
                    }
                    ++lineNumber;
                }
                fp.close();
            }
            catch (IOException e) {
            	console.display(String.format("Error: Problem reading the file: %s\n", m_fileName)); 
            }                               
        }
        catch (IOException e) {
        	console.display(String.format("Error: Unable to open file for reading: %s\n", m_fileName)); 
        }    	
    }    
    
	public boolean usesFileSystem() {
		return true;
	}	    
}
