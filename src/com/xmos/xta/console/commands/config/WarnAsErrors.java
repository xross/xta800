package com.xmos.xta.console.commands.config;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class WarnAsErrors extends Command {
	public WarnAsErrors() {
	}
	
    public int numArgs() {
    	return 0;
    }        

    public String name() {
    	return "Werror";
    }        

    public String commandLine() {
    	return String.format("Werror");
    }
    
    public String description(Engine engine, boolean detailed) {
        return "Werror: treat all warnings as errors"; 
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

        return new WarnAsErrors();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	engine.setWarnAsErrors();
    }    
}
