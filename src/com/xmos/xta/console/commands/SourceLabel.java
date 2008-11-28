package com.xmos.xta.console.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class SourceLabel extends Command {
	
	String m_fileName;
	boolean m_commandLineMode;
	
	SourceLabel() {
		m_fileName = "";
		m_commandLineMode = false;
	}
	
	SourceLabel(String fileName, boolean commandLineMode) {
		m_fileName = fileName;
		m_commandLineMode = commandLineMode;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "sourcelabel";
    }        

    public String commandLine() {
    	return "sourcelabel " + m_fileName;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "sourcelabel <file name>: Loads labels from the given file name";      	
    }
    
    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	String fileName = args[index + 1];
        return new SourceLabel(fileName, commandLineMode);        
    }
    
    public void execute(Shell shell, Engine engine, Console console) {    	
        try {
            BufferedReader fp = new BufferedReader(new FileReader(m_fileName));
                        
            try {
            	int lineNumber = 1;
                String line;
                while ((line = fp.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0) {  
                    	String[] tokens = line.trim().split("\\s+");
                    	if(tokens.length == 2){
                    		engine.addLabel(Integer.parseInt(tokens[0], 16), tokens[1]);
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
