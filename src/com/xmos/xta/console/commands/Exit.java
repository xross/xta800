package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Exit extends Command {
	Exit() {		
	}
	
    public int numArgs() {
    	return 0;
    }

    public String name() {
    	return "exit";
    }        

    public String commandLine() {
    	return "exit";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "exit: Quits the shell";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
    	return new Exit();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	shell.exit();
    }    
}
