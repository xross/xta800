package com.xmos.xta.console.commands.config;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class WarnAsErrors extends Command {
	private boolean m_errorOn;

	public WarnAsErrors() {
		m_errorOn = false;
	}
	
	public WarnAsErrors(boolean errorOn) {
		m_errorOn = errorOn;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "Werror";
    }        

    public String commandLine() {
    	if (m_errorOn) {
    		return String.format("Werror on");
    	} else {
    		return String.format("Werror off");
    	}
    }
    
    public String description(Engine engine, boolean detailed) {
        return "Werror <on / off>: treat all warnings as errors or not"; 
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

		if (args[index + 1].equals("on")) {
			return new WarnAsErrors(true);
		} else if (args[index + 1].equals("off")) {
			return new WarnAsErrors(false);
		} else {
			return null;
		}
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	engine.setWarnAsErrors(m_errorOn);
    }    
}
