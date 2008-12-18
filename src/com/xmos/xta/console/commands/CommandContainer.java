package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

abstract class CommandContainer extends Command {	
	private String m_commandName;
	private Command m_option;
	
	CommandContainer(String commandName) {
		m_commandName = commandName;
		m_option = null;
	}
	
	CommandContainer(String commandName, Command option) {
		m_commandName = commandName;
		m_option = option;
	}
	
	abstract protected Command[] getSubCommands();
	abstract protected String getDescription();
	abstract protected Command clone(Command opt);
	
    public int numArgs() {
    	return m_option.numArgs() + 1;
    }        

    public String name() {
    	return m_commandName;
    }        

    public String commandLine() {
    	return String.format("%s %s", m_option.commandLine(), m_commandName);
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("%s <option> <arg0> <arg1>...: %s", m_commandName, getDescription());
    	if (detailed) {    	    	
        	String optionsString = "\n";
        	for (Command option : getSubCommands()) {
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
    	for (Command option : getSubCommands()) {
    		if (arg.equals(option.name())) {
    			Command opt = option.clone(args, index + 1, commandLineMode);
    			if (opt == null)
    				return null;
    			
    			return clone(opt);
    		}    			
    	}
    	return null;
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	m_option.execute(shell, engine, console);
    }             
}
