package com.xmos.xta.core.console.commands.config;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;

public class ErrorAsWarn extends Command {
	private static final String[] COMMAND_NAMES = { "Ewarning" };

	private boolean m_warningOn;

	public ErrorAsWarn() {
		super(COMMAND_NAMES);
		m_warningOn = false;
	}

	public ErrorAsWarn(boolean warningOn) {
		super(COMMAND_NAMES);
		m_warningOn = warningOn;
	}

    public int numArgs() {
    	return 1;
    }

    public String commandLine() {
    	if (m_warningOn) {
    		return name() + " on";
    	} else {
    		return name() + " off";
    	}
    }

    public String description(Engine engine, boolean detailed) {
    	Config config = engine.getConfig();
        return name() + String.format(" <on/off>: treat errors as warnings or not (currently: %s)",
            	config.getWarningsAsErrors() ? "on" : "off");
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

		if (args[index + 1].equals("on")) {
			return new ErrorAsWarn(true);
		} else if (args[index + 1].equals("off")) {
			return new ErrorAsWarn(false);
		} else {
			return null;
		}
    }

    public void execute(Shell shell, Engine engine, Console console) {
    	engine.setErrorsAsWarnings(m_warningOn);
    }
}
