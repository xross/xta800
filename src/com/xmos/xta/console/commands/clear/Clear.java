package com.xmos.xta.console.commands.clear;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Clear extends Command  {
	public Clear() {		
	}
	
    public int numArgs() {
    	return 0;
    }

    public String name() {
    	return "clear";
    }        

    public String commandLine() {
    	return "clear";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "clear: Clears the screen (Gui mode only)";      	
    }

    public Command clone(String[] args, int index) {
    	return new Clear();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	console.clearScreen();
    }    
}

