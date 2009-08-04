package com.xmos.xta.console.commands.config;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;

public class TimingErrors extends Command {
	private boolean m_errorOn;

	public TimingErrors() {
		m_errorOn = false;
	}
	
	public TimingErrors(boolean errorOn) {
		m_errorOn = errorOn;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "Terror";
    }        

    public String commandLine() {
    	if (m_errorOn) {
    		return String.format("Terror on");
    	} else {
    		return String.format("Terror off");
    	}
    }
    
    public String description(Engine engine, boolean detailed) {
    	Config config = engine.getConfig();
        return String.format("Terror <on/off>: treat timing failures as errors or not (currently: %s)",
        	config.m_timingFailuresAsErrors ? "on" : "off"); 
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

		if (args[index + 1].equals("on")) {
			return new TimingErrors(true);
		} else if (args[index + 1].equals("off")) {
			return new TimingErrors(false);
		} else {
			return null;
		}
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	engine.setTimingFailuresAsErrors(m_errorOn);
    }    
}
