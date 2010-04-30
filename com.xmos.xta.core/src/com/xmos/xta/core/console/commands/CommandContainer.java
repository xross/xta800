package com.xmos.xta.core.console.commands;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.engine.Engine;

public abstract class CommandContainer extends Command {
	private Command m_option;

	public CommandContainer(String[] commandNames) {
		super(commandNames);
		m_option = null;
	}

	public CommandContainer(String[] commandNames, Command option) {
		super(commandNames);
		m_option = option;
	}

	abstract protected String getDescription();
	abstract protected Command clone(Command opt);

    public int numArgs() {
    	return m_option.numArgs() + 1;
    }

    public String commandLine() {
    	return String.format("%s %s", name(), m_option.commandLine());
    }

    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("%s <option> <arg0> <arg1>...: %s", name(), getDescription());
    	if (detailed) {
        	StringBuffer sb = new StringBuffer();
        	sb.append("\n");
        	for (Command option : getSubCommands()) {
        		sb.append("      option: ");
        		sb.append(option.description(engine, false));
        		sb.append( "\n");
        	}
        	String optionsString= sb.toString();
    		descString += optionsString.substring(0, optionsString.length()-1);
    	}
    	return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + 1) >= args.length)
			return null;

    	String arg = args[index + 1];
    	for (Command option : getSubCommands()) {
    		if (option.matches(arg)) {
    			Command opt = option.clone(args, index + 1);
    			if (opt == null)
    				return null;

    			return clone(opt);
    		}
    	}
    	return null;
    }

    public void execute(Shell shell, Engine engine, Console console) {
    	m_option.setSrc(getSrc());
    	m_option.execute(shell, engine, console);
    }    
}
