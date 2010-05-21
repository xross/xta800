package com.xmos.xta.core.console.commands.scripter;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.scripter.Scripter;

public class Rename extends Command {
	private static final String[] COMMAND_NAMES = { "rename" };

	private String m_from;
	private String m_to;

	public Rename() {
		super(COMMAND_NAMES);
	}

	public Rename(String from, String to) {
		super(COMMAND_NAMES);
		m_from = from;
		m_to = to;
	}

    public int numArgs() {
    	return 2;
    }

    public String commandLine() {
    	return name() + " " + m_from + " " + m_to;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <ANY> <TO_NAME>: Rename a mapping";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

        return new Rename(args[index + 1], args[index + 2]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		for (int i = 0; i < m_to.length(); i++) {
			if (Character.isSpaceChar(m_to.charAt(i))) {
	    		engine.notify(getSrc(), Notifier.NOTIFY_ERROR, "spaces not supported in labels");
		        return;
			}
		}

		Scripter scripter = engine.getScripter();
		scripter.createRefs();
		scripter.rename(getSrc(), engine, m_from, m_to);
    }
}

