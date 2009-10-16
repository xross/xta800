package com.xmos.xta.core.console;

import java.io.IOException;
import java.util.Vector;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.completors.XtaCompletor;
import com.xmos.xta.core.engine.Engine;

import jline.ConsoleReader;
import jline.Terminal;

public class JLineConsole extends Console {
	private ConsoleReader m_reader;
	private String m_prompt;
	private Engine m_engine;
	
	JLineConsole(Engine engine) throws IOException {
	  	m_engine = engine;
	  	initialise();
	}
	
	public void display(String str) {
		if (!redirectToFile(str)) {		
		    System.out.printf("%s", str);
		}
	}
	
	public void displayPrompt(String str) {
		m_prompt = str;
	}	
	
	public String getNextLine(Vector<Command> commandHistory) {
		String line = null;
		while (line == null) {
			try {
    			line = m_reader.readLine(m_prompt);	
	    		if (line == null) {
		   			reportError();
		    	    return null;
			    }

		    } catch (IOException e) {
	            line = null;
		    }
		    // The terminal needs resetting each time just 
		    // in case this process has been interrupted (Ctrl-Z). 
            Terminal.resetTerminal();
		    Terminal.setupTerminal();
		}
		return line;
	}

	public void clearScreen() {		
	}
	
	public boolean hasEscaped() {
		return false;
	}

	public void setFileRedirect(String fileName) {		
	}
	
	private void reportError() {
		System.out.printf("internal error: Problem reading console input (aborting)..\n");
	    System.out.printf("try using -DConsole=Basic\n");			
		System.exit(1);		
	}
	
	private void initialise() throws IOException {
		m_reader = new ConsoleReader();
		m_reader.setBellEnabled(false);
	  	m_reader.addCompletor(new XtaCompletor(m_engine));		
	}
}
