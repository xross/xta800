package com.xmos.xta.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


import jline.ArgumentCompletor;
import jline.Completor;
import jline.FileNameCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;

public class JLineCompletor implements Completor {
    private ArgumentCompletor m_baseCompletor;
    private Map<String, Completor> m_subCompletors;
    
	public JLineCompletor() {			
		m_subCompletors = new HashMap<String, Completor>();
		createCommandCompletors();
	}
	
	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, List candidates) {
		List<String> tokens = tokeniseBuffer(buffer);		
		if (tokens.size() > 1) {
			String lastCompleteCommand = tokens.get(tokens.size() - 2);

			if (m_subCompletors.containsKey(lastCompleteCommand)) {
				return m_subCompletors.get(lastCompleteCommand).complete(buffer, cursor, candidates);
			}
		} 		
		return m_baseCompletor.complete(buffer, cursor, candidates);
	}
		
	private void createCommandCompletors() {
	  	Command[] commands = CommandFactory.getCommands();
	  	String[] commandStrings = new String[commands.length];
	  	for (int index = 0; index < commands.length; ++index) {
            createSubCommandCompleters(commands[index]);
	  		commandStrings[index] = commands[index].name();
	  		
	  		if (commands[index].usesFileSystem()) {
	  			ArgumentCompletor argCompletor = createArgCompletor(new FileNameCompletor(), false);	  			
		  	    m_subCompletors.put(commands[index].name(), argCompletor);		  		  			
	  		}
	  	}
	  	m_baseCompletor = createArgCompletor(new SimpleCompletor(commandStrings), true);
	}
	
	private void createSubCommandCompleters(Command command) {				
		Command[] commands = command.getSubCommands();
	  	if (commands != null) {
     	  	String[] subCommandStrings = new String[commands.length];
	  	  	for (int index = 0; index < commands.length; ++index) {
	  	  	    subCommandStrings[index] = commands[index].name();
	  	        createSubCommandCompleters(commands[index]);	  		  	    
	  	  	}	  	  	
  	        ArgumentCompletor argCompletor = createArgCompletor(new SimpleCompletor(subCommandStrings), false);
	  	    m_subCompletors.put(command.name(), argCompletor);		  	
	  	}	  				
	}		
	
	private ArgumentCompletor createArgCompletor(Completor subCompletor, boolean addNullCompleter) {
        List<Completor> completors = new ArrayList<Completor>();
	    completors.add(subCompletor);
	    
	    if (addNullCompleter) {
	        completors.add(new NullCompletor());
	    }
	    ArgumentCompletor completor = new ArgumentCompletor(completors);
	    completor.setStrict(false);		
	    return completor;
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
}
