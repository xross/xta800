package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;

public class WarnAsErrors extends Command {
	private static final String[] COMMAND_NAMES = { "Werror" };

	private boolean m_errorOn;

	public WarnAsErrors() {
		super(COMMAND_NAMES);
		m_errorOn = false;
	}

	public WarnAsErrors(boolean errorOn) {
		super(COMMAND_NAMES);
		m_errorOn = errorOn;
	}

    public int numArgs() {
    	return 1;
    }

    public String commandLine() {
    	if (m_errorOn) {
    		return name() + " on";
    	} else {
    		return name() + " off";
    	}
    }

    public String description(Engine engine, boolean detailed) {
    	Config config = engine.getConfig();
        return name() + String.format(" <on/off>: treat warnings as errors or not (currently: %s)",
            	config.m_warnAsErrors ? "on" : "off");
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
