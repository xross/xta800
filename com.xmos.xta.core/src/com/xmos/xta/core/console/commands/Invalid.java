package com.xmos.xta.core.console.commands;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;

public class Invalid extends Command {
	private static final String[] COMMAND_NAMES = { "" };

	private String m_invalidCommand;
	private String m_errorMessage;

	public Invalid(String invalidCommand, String errorMessage, CommandSource source) {
		super(COMMAND_NAMES);
		m_invalidCommand = invalidCommand;
		m_errorMessage = errorMessage;
		setSrc(source);
	}

    public int numArgs() {
    	return 1;
    }

    public String commandLine() {
    	return m_invalidCommand;
    }

    public String description(Engine engine, boolean detailed) {
        return "";
    }

    public Command clone(String[] args, int index) {
    	assert false;
        return null;
    }

    public void execute(Shell shell, Engine engine, Console console) {
		engine.notify(getSrc(), Notifier.NOTIFY_ERROR, m_errorMessage);
    }
}
