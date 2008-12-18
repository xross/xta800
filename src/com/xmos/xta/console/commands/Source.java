package com.xmos.xta.console.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Source extends Command {
	private String m_fileName;
	private boolean m_commandLineMode;
	
	Source() {
		m_fileName = "";
		m_commandLineMode = false;
	}
	
	Source(String fileName, boolean commandLineMode) {
		m_fileName = fileName;
		m_commandLineMode = commandLineMode;
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
        return new Source(fileName, commandLineMode);        
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
                        
                        Command command = CommandFactory.buildCommand(
                        		tokens, 0, shell.getCommandHistory(), m_commandLineMode);
                        
                        if (command != null) {
                        	shell.addCommand(command, location+1);
                        	location++;
                        }
                        else {
                        	if(!tokens[0].startsWith("#")){
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
}
