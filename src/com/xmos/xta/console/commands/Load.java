package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Load extends Command {
	private String m_executableName;
	
	Load() {
		m_executableName = "";
	}
	
	Load(String executableName) {
		m_executableName = executableName;
	}
	
    public int numArgs() {
    	return 1;
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
    
    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	String executableName = args[index + 1];
        return new Load(executableName);        
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (engine.loadExecutable(m_executableName) == false) {
        	console.display(String.format("Error: Failed to load executable: %s\n", m_executableName));
        }
    }

	public String toString() {
		return commandLine();
	}    
}
