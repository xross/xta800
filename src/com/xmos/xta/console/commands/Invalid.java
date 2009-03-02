package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class Invalid extends Command {
	private String m_invalidCommand;
	private String m_errorMessage;
		
	public Invalid(String invalidCommand, String errorMessage) {
		m_invalidCommand = invalidCommand;
		m_errorMessage = errorMessage;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "";
    }        

    public String commandLine() {
    	return m_invalidCommand;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	assert false;
        return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	console.display(m_errorMessage);
    }    
}
