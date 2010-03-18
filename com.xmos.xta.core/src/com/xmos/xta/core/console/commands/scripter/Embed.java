package com.xmos.xta.core.console.commands.scripter;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.scripter.Scripter;

public class Embed extends Command {
	private static final String[] COMMAND_NAMES = { "embed" };

	private String m_fileName;

	public Embed() {
		super(COMMAND_NAMES);
	}

	public Embed(String fileName) {
		super(COMMAND_NAMES);
		m_fileName = fileName;
	}

    public int numArgs() {
    	return 1;
    }

    public String commandLine() {
    	return name() + " " + m_fileName;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <FILENAME>: Embed the script into the designated file - also embed active pragmas into source";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

        return new Embed(args[index + 1]);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Scripter scripter = engine.getScripter();
		scripter.setEmbedIntoSource(true);
		scripter.setEmbedFileName(m_fileName);
		scripter.dump();
    }
}

