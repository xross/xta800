package com.xmos.xta.console.completors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandFactory;
import com.xmos.xta.engine.Engine;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.FileNameCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;

public class XtaCompletor implements Completor {
    private ArgumentCompletor m_baseCompletor;
    private Map<String, Completor> m_subCompletors;
    private Engine m_engine;
    
	public XtaCompletor(Engine engine) {			
		m_engine = engine;
		m_subCompletors = new HashMap<String, Completor>();
		createCommandCompletors();		
	}
	
	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, List candidates) {
		List<String> tokens = tokeniseBuffer(buffer);		
		if (tokens.size() > 1) {
			String lastCompleteCommand = getLastCompleteCommand(tokens.iterator(), 
					CommandFactory.getCommands(), "");			

			if (m_subCompletors.containsKey(lastCompleteCommand)) {				
				return m_subCompletors.get(lastCompleteCommand).complete(buffer, cursor, candidates);
			} else {
			    return 0;
			}
		} else {		
		    return m_baseCompletor.complete(buffer, cursor, candidates);
		}		
	}

	private void createCommandCompletors() {
	  	Command[] commands = CommandFactory.getCommands();
	  	String[] commandStrings = new String[commands.length];
	  	for (int index = 0; index < commands.length; ++index) {
            createSubCommandCompleters(commands[index], 1);
	  		commandStrings[index] = commands[index].name();	  		
	  	}
	  	m_baseCompletor = createArgCompletor(new SimpleCompletor(commandStrings), 0);
	}
	
	private void createSubCommandCompleters(Command command, int commandLevel) {				
		Command[] commands = command.getSubCommands();
	  	if (commands.length > 0) {
	  		// Adds the completors for the sub-commands of this command.
     	  	String[] subCommandStrings = new String[commands.length];
	  	  	for (int index = 0; index < commands.length; ++index) {
	  	  	    subCommandStrings[index] = commands[index].name();
	  	        createSubCommandCompleters(commands[index], commandLevel + 1);	  		  	    
	  	  	}
  	        ArgumentCompletor argCompletor = createArgCompletor(
  	        		new SimpleCompletor(subCommandStrings), commandLevel);
	  	    m_subCompletors.put(command.name(), argCompletor);
	  	} else {	  	    	  		  		  	
	  		// Adds the completors for the options of this (leaf) sub-command.
  		    List<Completor> completors = new ArrayList<Completor>();
	  	    for (Command.ArgType argType : command.getArgTypes()) {
	  		    switch (argType) {
	  		    case FILE:
	  			    completors.add(new FileNameCompletor());	  			
	  		        break;
			    
	  		    case FUNCTION:
	  			    completors.add(new FunctionCompletor(m_engine));	  			
			        break;
			    
	  		    case ENDPOINT:
	  			    completors.add(new CodeReferenceCompletor(m_engine, argType));	  			
			        break;
	  			
	  		    case SRCLABEL:
	  			    completors.add(new CodeReferenceCompletor(m_engine, argType));	  			
			        break;
			        
	  		    case HELP:
	  			    completors.add(new HelpCompletor());	  			
			        break;
			        
			    default:
				    assert(false);
			        break;
	  		    }
	  	    }

    	  	if (completors.size() > 0) {
  		        ArgumentCompletor argCompletor = createArgCompletor(completors, commandLevel);
	            m_subCompletors.put(command.name(), argCompletor);
	  	    }
	  	}
	}

	private ArgumentCompletor createArgCompletor(Completor subCompletor, int commandLevel) {		
		List<Completor> subCompletors = new ArrayList<Completor>();
		subCompletors.add(subCompletor);		
		return createArgCompletor(subCompletors, commandLevel);
	}
	
	private ArgumentCompletor createArgCompletor(List<Completor> subCompletors, int commandLevel) {		
		addNullCompletors(subCompletors, commandLevel);  		    
    	subCompletors.add(new NullCompletor());
	    ArgumentCompletor completor = new ArgumentCompletor(subCompletors);
	    completor.setStrict(false);		
	    return completor;
	}
	
	private void addNullCompletors(List<Completor> completors, int commandLevel) {
		// This adds the required amount of null pointers to the start of the 
		// given completors list. This is too account for the fact that the arguments
		// to this command will not start at zero, due to the command nesting.
	    int numNullCommands = 0;
		while (numNullCommands < commandLevel) {
			completors.add(numNullCommands, null);
			++numNullCommands;
		}
    }

	private List<String> tokeniseBuffer(String buffer) {
		  StringTokenizer st = new StringTokenizer(buffer);
		  List<String> tokens = new ArrayList<String>();
		  while (st.hasMoreTokens ()) {
		    tokens.add(st.nextToken());
		  }
		  if (buffer.endsWith(" ")) {
			  tokens.add("");
		  }		  
		  return tokens;
	}
	
	private String getLastCompleteCommand(Iterator<String> iter, 
			Command[] commands, String lastCommandName) {
				
		if (iter.hasNext()) {			
	        String token = iter.next();		
	        
		    for (Command command : commands) {
			    if (command.name().equals(token)) {
			        return getLastCompleteCommand(iter, command.getSubCommands(), token);
			    }
		    }
		}
	    return lastCommandName;		
	}
}
