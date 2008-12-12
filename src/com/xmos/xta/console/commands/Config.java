package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

class Config extends Command {
	static private Command[] m_options = { 
		new Proc(), 
		new Freq(), 
		new Threads(), 
		new Iterations(), 
		new Mode(), 
	};
	
	Command m_option;
	
	Config() {
		m_option = null;
	}
	
	Config(Command option) {
		m_option = option;
	}
	
    public int numArgs() {
    	return m_option.numArgs() + 1;
    }        

    public String name() {
    	return "config";
    }        

    public String commandLine() {
    	return String.format("config %s", m_option.commandLine());
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("config <option> <arg0> <arg1>...: Configures the given option");
    	if (detailed) {    	    	
        	String optionsString = "\n";
        	for (Command option : m_options) {
        		optionsString += "      option: " + option.description(engine, true) + "\n";
        	}  
    		descString += optionsString.substring(0, optionsString.length()-1);
    	}
    	return descString;
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + 1) >= args.length) 
			return null;		

    	String arg = args[index + 1];    	
    	for (Command option : m_options) {
    		if (arg.equals(option.name())) {
    			Command opt = option.clone(args, index + 1, commandLineMode);
    			return new Config(opt);
    		}    			
    	}
    	return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	m_option.execute(shell, engine, console);
    }             
}
