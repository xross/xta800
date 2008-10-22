package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Pwd extends Command {
	Pwd() {
	}
	
    public int numArgs() {
    	return 0;
    }        
	
    public String name() {
    	return "pwd";
    }        
	
    public String commandLine() {
    	return "pwd";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "pwd: Displays the current working directory";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        return new Pwd();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	String currentDirectory = System.getProperty("user.dir");
    	console.display(String.format("%s\n", currentDirectory));
    }    
}
