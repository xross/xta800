package com.xmos.xta.console;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandFactory;
import com.xmos.xta.console.commands.Invalid;
import com.xmos.xta.engine.Engine;

public class Shell {
	enum State {
		RUNNING,
		EXIT,
	}
	
	private boolean m_exitAllowed;
	private State m_state;
	private Console m_console;
    private Engine m_engine;
    private Queue<Command> m_commands;
    private Vector<Command> m_commandHistory;
		
	public Shell(Console console, Engine engine, String[] args) {
	    initialise(console, engine);
    	processCommandLine(args);	   
	    m_exitAllowed = true;
	}

	public Shell(Console console, Engine engine, boolean exitAllowed) {
	    initialise(console, engine);
	    m_exitAllowed = exitAllowed;
	}

    public void execute() {    	
    	while (!isComplete()) {
    		Command command = getNextCommand();
    		command.execute(this, m_engine, m_console);
    		m_commands.remove(command);
    	}
    }
    
    public void exit() {        
    	m_state = State.EXIT;
    }

    public Vector<Command> getCommandHistory() {
    	return m_commandHistory;
    }

    public void addCommand(Command command) {
    	m_commands.add(command);
    }
    
    public int getLocation(Command command){
    	ArrayList<Command> com = new ArrayList<Command>();
    	com.addAll(m_commands);
    	return com.indexOf(command);
    }
    
    public void addCommand(Command command, int position) {
    	if(position == -1){
    		addCommand(command);
    	} else {
			ArrayList<Command> com = new ArrayList<Command>();
			com.addAll(m_commands);
			com.add(position, command);
			m_commands.clear();
			m_commands.addAll(com);
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
    		m_commandHistory.add(new Invalid(historyString));
    	}    	    	
    }
    
    private void initialise(Console console, Engine engine) {
	    m_engine = engine;
		m_console = console;
		m_state = State.RUNNING;
	    m_commands = new LinkedList<Command>();
	    m_commandHistory = new Vector<Command>();
    }
    
    private void processCommandLine(String[] args) {     		    
	    for (int i = 0; i < args.length; ++i) {
	    	Command command = CommandFactory.buildCommand(args, i, m_commandHistory, true);
	    	if (command == null)
	    		usage();
	    	
	    	m_commands.offer(command);
	    	i += command.numArgs();
	    }	
    }
    
    private Command getNextCommand() {
		Command command = m_commands.peek();
		if (command != null) {
    		m_commandHistory.add(command);			
			return command;	
		}
    	return getCommandFromConsole();
    }
    boolean active = true;
        
    private Command getCommandFromConsole() {
    	Command command = null;
    	do {
    		m_console.display(">");    		    		
            String line = m_console.getNextLine(m_commandHistory);
            line = line.trim();
            if (line.length() > 0) {            
                String[] tokens = line.trim().split("\\s+");
                
                ArrayList<String> s = new ArrayList<String>();
                
                boolean multiword = false;
                String m = "";
                for(String word : tokens){
                	if(word.startsWith("\"")){
                		multiword = true;
                		word = word.substring(1);
                	} 
                	if(word.endsWith("\"")){
                		multiword = false;
                		word = word.substring(0, word.length()-1);
                		word = m+ " " + word ;
                		word = word.trim();
                		m = "";
                	} 
                	if(multiword){
                		m = m + " " + word;
                	} else {
                		s.add(word);
                	}
                	
                }
                tokens = s.toArray(tokens);
                command = CommandFactory.buildCommand(tokens, 0, m_commandHistory, false);         
                if (command == null) {
            		m_commandHistory.add(new Invalid(line));                	
                	m_console.display("Invalid command (Try 'help'..)\n");
                }
                if (!exitAllowed(command)) {
            		command = null;
                }
            }            
    	} while (command == null);
    	
		m_commandHistory.add(command);                    	
		return command;    	
    }
        
    private boolean isComplete() {
    	return m_state == State.EXIT;
    }
           
    private boolean exitAllowed(Command command) {
        if (command == null)
            return false;
        
        if (!m_exitAllowed &&
        	!m_console.wasInternal() &&
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

		        
}
