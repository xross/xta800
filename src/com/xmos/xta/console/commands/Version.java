package com.xmos.xta.console.commands;

import org.eclipse.core.runtime.Platform;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Version extends Command {
	Version() {		
	}
	
    public int numArgs() {
    	return 0;
    }

    public String name() {
    	return "version";
    }        

    public String commandLine() {
    	return "version";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "version: Displays the version information";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	return new Version();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	String version = Platform.getProduct().getProperty("aboutText");
    	
    }    
}
