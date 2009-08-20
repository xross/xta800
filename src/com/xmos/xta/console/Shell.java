package com.xmos.xta.console;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandFactory;
import com.xmos.xta.console.commands.Invalid;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.CommandSource.Type;

public class Shell {
	enum State {
		RUNNING,
		EXIT,
	}
	
	private boolean m_exitAllowed;
	private State m_state;
	private Console m_console;
    private Notifier m_notifier;
	private Notifier m_savedNotifier;
    private Engine m_engine;
    private Queue<Command> m_commands;
    private Vector<Command> m_commandHistory;
    private String m_prompt;
	private int m_numTimingViolations;
		
	public Shell(Console console, Engine engine, Notifier notifier, String[] args) {
	    initialise(console, engine, notifier);
    	processCommandLine(args);	   
	    m_exitAllowed = true;
	}

	public Shell(Console console, Engine engine, Notifier notifier) {
	    initialise(console, engine, notifier);
	    m_exitAllowed = false;
	}

    public int execute() {    	
    	while (!isComplete()) {
    		Command command = getNextCommand();
    		
            if (exitAllowed(command)) {
            	preCommandExecute();
            	command.execute(this, m_engine, m_console);
            	postCommandExecute();            	
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
    
    public void addToCommandHistory(Command command) {
    	m_commandHistory.add(command);
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
    
    public Vector<String> getHistoryStrings() {
    	Vector<String> historyStrings = new Vector<String>();    	
    	for (Command command : m_commandHistory) {
    		historyStrings.add(command.commandLine());
    	}    	
    	return historyStrings;
    }

    public void setHistoryStrings(Vector<String> historyStrings) {
    	for (String historyString : historyStrings) {
    		m_commandHistory.add(new Invalid(historyString, "", null));
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
    }
    
    private void processCommandLine(String[] args) {
	    for (int i = 0; i < args.length; ++i) {
            CommandSource src = new CommandSource(Type.CMD);
	    	Command command = CommandFactory.buildCommand(src, args, i, m_commandHistory, m_console);
	    	if (command == null) {
	    		usage();
	    	}
	    	
	    	m_commands.offer(command);
	    	i += command.numArgs();
	    }	
    }
    
    private Command getNextCommand() {
		Command command = m_commands.peek();
		if (command != null) {
			if (command.getSrc().m_type != Type.FILE) {
    		    m_commandHistory.add(command);
			}
        	m_commands.remove(command);
			return command;	
		}
		return getCommandFromConsole();
    }
        
    private Command getCommandFromConsole() {
    	Command command = null;
    	while (command == null) {
    		m_prompt = "xta " + (m_commandHistory.size() + 1) + ">";
    		m_console.displayPrompt(m_prompt);    	
    		
            String line = m_console.getNextLine(m_commandHistory);
            line = line.trim();
            if (line.length() > 0) {            	
                String[] tokens = line.trim().split("\\s+");                
                ArrayList<String> tokenGroups = groupQuotedTokens(tokens);
                tokens = tokenGroups.toArray(tokens);
                
                boolean isPlingCommand = CommandFactory.isPlingCommand(tokens[0]);
                CommandSource src = new CommandSource(Type.CONSOLE);
                command = CommandFactory.buildCommand(src, tokens, 0, m_commandHistory, m_console);

                // Check command is valid and has consumed entire line
                boolean invalidCommand = false;
                if (command != null) {
                	if (isPlingCommand) {
                		invalidCommand = (tokens.length != 1);
                	} else {
                		invalidCommand = (command.numArgs() != (tokens.length - 1));
                	}
                } else {
                	invalidCommand = true;
                }
                
                if (invalidCommand) {
                	command = new Invalid(line, "Invalid command (Try 'help'..)\n", src);
                }
            }
    	}    	
		m_commandHistory.add(command);                    	
		return command;    	
    }

	private ArrayList<String> groupQuotedTokens(String[] tokens) {
        ArrayList<String> s = new ArrayList<String>();                

		boolean multiword = false;
		String m = "";
		for (String word : tokens){
			if (word.startsWith("\"")){
				multiword = true;
				word = word.substring(1);
			} 
			if (word.endsWith("\"")){
				multiword = false;
				word = word.substring(0, word.length()-1);
				word = m+ " " + word ;
				word = word.trim();
				m = "";
			} 
			if (multiword){
				m = m + " " + word;
			} else {
				s.add(word);
			}
		}
		return s;
	}
        
    private boolean isComplete() {
    	return m_state == State.EXIT;
    }
           
    private boolean exitAllowed(Command command) {
        assert(command != null);
        if (!m_exitAllowed &&
        	command.name().equals("exit")) {
    		return false;
        }
    	return true;
    }
    
	private void usage() {
		m_console.display("Avaliable console commands:\n");	        	
        
      	Command[] avaliableCommands = CommandFactory.getCommands();
      	for (Command command : avaliableCommands) {
      		m_console.display(String.format("  %s\n", command.description(m_engine, false)));   		
      	}
        System.exit(1);
	}
		
	private void preCommandExecute() {
		m_savedNotifier = m_engine.getNotifier();
    	m_engine.setNotifier(m_notifier);
    	
    	if (m_engine.getConfig().m_verbosity >= 10)
    		m_engine.setMonitor(new ConsoleProgessMonitor(m_console));                           		
	}
	
	private void postCommandExecute() {
    	m_engine.setNotifier(m_savedNotifier);
    	m_engine.setMonitor(null);                           		            			
	}
}
