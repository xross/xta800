package com.xmos.xta.core.console;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import com.xmos.libs.util.StringUtil;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandFactory;
import com.xmos.xta.core.console.commands.Invalid;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;

public class Shell {
	enum State {
		RUNNING,
		EXIT,
	}

	private State m_state;
	private Console m_console;
    private Notifier m_notifier;
	private Notifier m_savedNotifier;
    private Engine m_engine;
    private Queue<Command> m_commands;
    private Vector<Command> m_commandHistory;
    private String m_prompt;
	private int m_numTimingViolations;
	private boolean m_executeArgsOnly;

	public Shell(Console console, Engine engine, Notifier notifier, String[] args) {
	    initialise(console, engine, notifier);
        CommandSource src = new CommandSource(Type.COMMAND_LINE);
    	processCommands(args, src);
	}

	public Shell(Console console, Engine engine, Notifier notifier) {
	    initialise(console, engine, notifier);
	}

	public void execute(String commandStr, CommandSource src) {
		m_executeArgsOnly = true;
        String[] tokens = commandStr.trim().split("\\s+");
        ArrayList<String> tokenGroups = StringUtil.groupQuotedTokens(tokens);
    	processCommands(tokenGroups.toArray(new String[] {}), src);
    	execute();
	}

    public int execute() {
    	while (!isComplete()) {
    		Command command = getNextCommand();
           	if (preCommandExecute(command)) {
              	command.execute(this, m_engine, m_console);
              	postCommandExecute(command);
           	}
    	}
    	return m_numTimingViolations;
    }

	public void exit(int numTimingViolations) {
    	m_state = State.EXIT;
    	m_numTimingViolations = numTimingViolations;
    }

    public Vector<Command> getCommandHistory() {
    	return m_commandHistory;
    }

    public void addCommand(Command command) {
    	m_commands.add(command);
    }

    public int getLocation(Command command){
    	ArrayList<Command> commands = new ArrayList<Command>();
    	commands.addAll(m_commands);
    	return commands.indexOf(command);
    }

    public void addCommand(Command command, int position) {
    	if (position == -1) {
    		addCommand(command);
    	} else {
			ArrayList<Command> commands = new ArrayList<Command>();
			commands.addAll(m_commands);
			commands.add(position, command);
			m_commands.clear();
			m_commands.addAll(commands);
    	}
    }

    public String getPrompt() {
		return m_prompt;
	}

    private void initialise(Console console, Engine engine, Notifier notifier) {
	    m_engine = engine;
		m_console = console;
	    m_notifier = notifier;
		m_state = State.RUNNING;
	    m_commands = new LinkedList<Command>();
	    m_commandHistory = new Vector<Command>();
	    m_executeArgsOnly = false;
    }

    private void processCommands(String[] args, CommandSource src) {
	    for (int i = 0; i < args.length; ++i) {
	    	Command command = CommandFactory.buildCommand(src, args, i, m_commandHistory, m_console);
	    	if (command == null) {
	    		if (!m_executeArgsOnly) {
	    			usage();
	    		} else {
	    			return;
	    		}
	    	}

	    	m_commands.offer(command);

	    	i += (command.getFileRedirect() != null) ?
	    			command.numArgs() + 2 :
	    			command.numArgs();
	    }
    }

    private Command getNextCommand() {
    	if (m_commands.size() == 0)
    		fillCommandQueueFromConsole();

		Command command = m_commands.peek();
		assert(command != null);

		if ((command.getSrc().getType() != Type.FILE) &&
			(command.getSrc().getType() != Type.SRC_LEVEL_COMMAND)) {
    	    m_commandHistory.add(command);
		}
        m_commands.remove(command);
		return command;
    }

    private void fillCommandQueueFromConsole() {
    	while (m_commands.size() == 0) {
    		m_prompt = "xta " + (m_commandHistory.size() + 1) + ">";
    		m_console.displayPrompt(m_prompt);

    		ConsoleCommand consoleCommand = m_console.getNextLine(m_commandHistory);

            String[] lines = consoleCommand.getStr().split("\n");
            for (String line : lines) {
                line = line.trim();

                if (line.startsWith("#") || line.length() == 0) {
                	continue; // Ignore comment and blank lines...
                }

                String[] tokens = line.trim().split("\\s+");
                ArrayList<String> tokenGroups = StringUtil.groupQuotedTokens(tokens);
                if (tokenGroups.size() > 0) {
                    tokens = tokenGroups.toArray(new String[0]);
                    Command command = CommandFactory.buildCommand(
                    		consoleCommand.getSrc(), tokens, 0, m_commandHistory, m_console);

                    if (!commandIsValid(command, tokens)) {
                    	command = new Invalid(line, "Invalid command (Try 'help'..)", consoleCommand.getSrc());
                    }
                    m_commands.offer(command);
                }
            }
    	}
    }

    private boolean commandIsValid(Command command, String[] tokens) {
    	if (command == null)
    		return false;

       	if (CommandFactory.isPlingCommand(tokens[0]))
       		return (tokens.length == 1);

   		if (command.getFileRedirect() != null)
   			return ((command.numArgs() + 2) == (tokens.length - 1));

   		return (command.numArgs() == (tokens.length - 1));
    }

    private boolean isComplete() {
    	return ((m_executeArgsOnly && (m_commands.size() == 0)) ||
    	        (m_state == State.EXIT));
    }

	private void usage() {
		m_console.display("Avaliable console commands:\n");

      	Command[] avaliableCommands = CommandFactory.getCommands();
      	for (Command command : avaliableCommands) {
      		m_console.display(String.format("  %s\n", command.description(m_engine, false)));
      	}
        System.exit(1);
	}

	private boolean preCommandExecute(Command command) {
		m_savedNotifier = m_engine.getNotifier();
    	m_engine.setNotifier(m_notifier);

    	if (m_engine.getVerbosity() >= Config.VERBOSITY_PROGRESS_MONITOR_ENABLED)
    		m_engine.setMonitor(new ConsoleProgessMonitor(m_console));

    	if (command.getFileRedirect() != null) {
    		if (!m_console.openRedirectFile(command.getFileRedirect(), command.getFileRedirectMode(), command.getSrc(), m_notifier)) {
    			return false;
    		}
    	}    	
    	return true;
	}

	private void postCommandExecute(Command command) {
    	m_engine.setNotifier(m_savedNotifier);

    	if (!m_executeArgsOnly) {
    		m_engine.setMonitor(null);
    	}

    	if (command.getFileRedirect() != null) {
    		m_console.closeRedirectFile();
    	}

    	if (m_console.hasEscaped()) {
    		m_commands.clear();
    	}
	}
}
