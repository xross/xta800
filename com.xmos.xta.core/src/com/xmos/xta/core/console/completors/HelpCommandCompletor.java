package com.xmos.xta.core.console.completors;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandFactory;

import jline.Completor;

public class HelpCommandCompletor implements Completor {
	private XtaArgumentCompletor m_parent;
	
	public HelpCommandCompletor() {
		m_parent = null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int complete(String buffer, int cursor, List candidates) {
        String start = (buffer == null) ? "" : buffer;
                
        List<String> bufferTokens = tokeniseBuffer(m_parent.getBuffer());
        if (bufferTokens.size() >= 2) {
        	String commandStr = bufferTokens.get(1);

    	  	for (Command command : CommandFactory.getCommands()) {
    	  		if (commandStr.equals(command.name())) {
    	  			for (Command subCommand : command.getSubCommands()) {
    	                if (subCommand.name().startsWith(start)) {
    	            	   	candidates.add(subCommand.name());
    	            	}        		
    	  				
    	  			}
    	  		}
    	  	}
        }        
		return 0;
	}
	
	public void setParent(XtaArgumentCompletor parent) {
		m_parent = parent;
	}
	
	private List<String> tokeniseBuffer(String buffer) {
		  StringTokenizer st = new StringTokenizer(buffer.trim());
		  List<String> tokens = new ArrayList<String>();
		  while (st.hasMoreTokens()) {
		    tokens.add(st.nextToken());
		  }
		  return tokens;
	}
}
