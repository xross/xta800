package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Echo extends Command {
	
	private String m_string;
	
	Echo() {		
	}
	
	Echo(String string) {
		m_string = string;
	}
		
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "echo";
    }

    public String commandLine() {
    	return "echo";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "echo \"text\": prints the text to the console";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        return new Echo(args[index + 1]);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	console.display(m_string + "\n");
    }    
}