package com.xmos.xta.core.console;

import java.util.Vector;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandFactory;
import com.xmos.xta.core.console.commands.Invalid;
import com.xmos.xta.core.engine.CommandRunner;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;

public class ConsoleCommandRunner implements CommandRunner {
	private Shell m_shell;
	private Engine m_engine;
	private Console m_console;
	
	public ConsoleCommandRunner(Shell shell, Engine engine, Console console) {
		m_shell = shell;
		m_engine = engine;
		m_console = console;
	}
	
	public void run(String command, CommandSource src, int commandNum) {
		runSectionCommand(command, src);		
	}

    private void runSectionCommand(String commandStr, CommandSource src) {
        String[] tokens = commandStr.trim().split("\\s+");

        Command command = CommandFactory.buildCommand(src, tokens, 0, new Vector<Command>(), m_console);

        if (command == null) {
          	command = new Invalid(commandStr,
         		    String.format("invalid command in source file '%s'", commandStr), src);
        }

	    command.execute(m_shell, m_engine, m_console);
    }
}
