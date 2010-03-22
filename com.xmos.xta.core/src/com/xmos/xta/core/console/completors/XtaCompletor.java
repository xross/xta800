package com.xmos.xta.core.console.completors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.FileNameCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;

import com.xmos.libs.util.NonIteratableMap;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandFactory;
import com.xmos.xta.core.engine.Engine;

public class XtaCompletor implements Completor {
	private static final boolean DEBUG = false;

    private ArgumentCompletor m_baseCompletor;
    private Map<String, Completor> m_subCompletors;
    private Engine m_engine;

	public XtaCompletor(Engine engine) {
		m_engine = engine;
		m_subCompletors = new NonIteratableMap<String, Completor>();
		createCommandCompletors();
		if (DEBUG) displayCommandCompletors();
	}

	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, List candidates) {
		List<String> tokens = tokeniseBuffer(buffer);
		if (tokens.size() > 1) {
			String lastCompleteCommand = getCompleteCommandString(tokens.iterator(),
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
            createSubCommandCompleters(commands[index], 1, "");
	  		commandStrings[index] = commands[index].name();
	  	}
	  	m_baseCompletor = createArgCompletor(new SimpleCompletor(commandStrings), 0);
	}

	private void createSubCommandCompleters(Command command, int commandLevel, String prefix) {
        if (DEBUG) {
        	ouptutIndent(commandLevel);
        	System.out.printf("-> %s, level: %d ", command.name(), commandLevel);
        }

		Command[] commands = command.getSubCommands();
	  	if (commands.length > 0) {
	  		// Adds the completors for the sub-commands of this command.
	        if (DEBUG) System.out.printf("(NODE)\n");

     	  	String[] subCommandStrings = new String[commands.length];
	  	  	for (int index = 0; index < commands.length; ++index) {
	  	  	    subCommandStrings[index] = commands[index].name();
	  	        createSubCommandCompleters(commands[index], commandLevel + 1, command.name());
	  	  	}
  	        ArgumentCompletor argCompletor = createArgCompletor(
  	        		new SimpleCompletor(subCommandStrings), commandLevel);
	  	    m_subCompletors.put(getCompleteCommandString(prefix, command), argCompletor);

	  	    if (DEBUG) System.out.printf("    %s\n", argCompletor.toString());

	  	} else {
	  		// Adds the completors for the options of this (leaf) sub-command.
	        if (DEBUG) System.out.printf("(LEAF) ");

  		    List<Completor> completors = new ArrayList<Completor>();
	  	    for (Command.ArgType argType : command.getArgTypes()) {
	  		    switch (argType) {
	  		    case FILE:
	  		    	if (DEBUG) System.out.printf("[file]");
	  			    completors.add(new FileNameCompletor());
	  		        break;

	  		    case FUNCTION:
	  		    	if (DEBUG) System.out.printf("[function]");
	  			    completors.add(new FunctionCompletor(m_engine));
			        break;

	  		    case ALLREFS:
	  		    	if (DEBUG) System.out.printf("[allrefs]");
	  			    completors.add(new CodeReferenceCompletor(m_engine, argType));
			        break;

	  		    case ENDPOINT:
	  		    	if (DEBUG) System.out.printf("[endpoint]");
	  			    completors.add(new CodeReferenceCompletor(m_engine, argType));
			        break;

	  		    case LABEL:
	  		    	if (DEBUG) System.out.printf("[label]");
	  			    completors.add(new CodeReferenceCompletor(m_engine, argType));
			        break;

	  		    case HELP:
	  		    	if (DEBUG) System.out.printf("[help]");
	  			    completors.add(new HelpCompletor());
			        break;

	  		    case IGNORE:
	  		    	if (DEBUG) System.out.printf("[ignore]");
	  		    	completors.add(new NullCompletor());
	  		    	break;

			    default:
				    assert(false);
			        break;
	  		    }
	  	    }
	  	    if (DEBUG) System.out.printf(" (%d) ", completors.size());

    	  	if (completors.size() > 0) {
  		        ArgumentCompletor argCompletor = createArgCompletor(completors, commandLevel);
	            m_subCompletors.put(getCompleteCommandString(prefix, command), argCompletor);

		  	    if (DEBUG) System.out.printf(" %s", argCompletor.toString());
    	  	}
	  	    if (DEBUG) System.out.printf("\n");
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
			completors.add(numNullCommands, new NullCompletor());
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

	private String getCompleteCommandString(Iterator<String> iter,
			Command[] commands, String lastCommandName) {

		if (iter.hasNext()) {
	        String token = iter.next();

		    for (Command command : commands) {
			    if (command.name().equals(token)) {
			    	String dash = (lastCommandName.length() > 0) ? "-" : "";
			        return lastCommandName + dash + getCompleteCommandString(iter, command.getSubCommands(), token);
			    }
		    }
		}
	    return lastCommandName;
	}

	private String getCompleteCommandString(String prefix, Command command) {
		String dash = prefix.length() > 0 ? "-" : "";
		return prefix + dash + command.name();
	}

	private void ouptutIndent(int level) {
		int i = level;
		while (i > 1) {
			System.out.printf("    ");
			--i;
		}
	}

	private void displayCommandCompletors() {
		System.out.printf("baseCompletor: %s\n", m_baseCompletor.toString());
		for (String key : m_subCompletors.keySet()) {
			System.out.printf("  %s %s\n", key, m_subCompletors.get(key).toString());
		}
	}
}
