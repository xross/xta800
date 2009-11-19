package com.xmos.xta.core.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.completors.XtaCompletor;
import com.xmos.xta.core.engine.Engine;

import jline.ConsoleOperations;
import jline.ConsoleReader;

public class JLineConsole extends Console {
	private ConsoleReader m_reader;
	private String m_prompt;
	private Engine m_engine;
	private boolean m_hasEscaped;
	
	JLineConsole(Engine engine) throws IOException {
	  	m_engine = engine;
	  	m_hasEscaped = false;
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
		}
		m_hasEscaped = false;
		return line;
	}

	public void clearScreen() {		
	}
	
	public boolean hasEscaped() {
		return m_hasEscaped;
	}

	public void setHasEscaped() {
		m_hasEscaped = true;
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
	  	
//	  	m_reader.addTriggeredAction(ConsoleOperations.CTRL_C, new ControlCListener(this));
	}
	
/*	class ControlCListener implements ActionListener {
		private JLineConsole m_console;
		
		ControlCListener(JLineConsole console) {
			m_console = console;
		}
		
		public void actionPerformed(ActionEvent e) {
			m_console.setHasEscaped();
		}
	}*/
}
