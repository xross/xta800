package com.xmos.xta.console;

import java.io.IOException;
import java.util.Vector;

import com.xmos.xta.console.commands.Command;

import jline.ConsoleReader;

public class JLineConsole implements Console {
	private ConsoleReader m_reader;
	private String m_prompt;
	
	JLineConsole() throws IOException {
		m_reader = new ConsoleReader();
		m_reader.setBellEnabled(false);
	  	m_reader.addCompletor(new JLineCompletor());
	}
	
	public void display(String str) {
		System.out.printf("%s", str);
	}
	
	public void displayPrompt(String str) {
		m_prompt = str;
	}	
	
	public String getNextLine(Vector<Command> commandHistory) {
		try {
			String line = m_reader.readLine(m_prompt);			
			if (line == null) {
				reportError();
			    return null;
			}
			return line;
		} catch (IOException e) {
			System.out.printf("Internal Error: Problem reading console input (aborting)..\n");
		    System.out.printf("Try using -DConsole=Basic\n");			
			System.exit(1);
			return null;
		}
	}
	
	public boolean wasInternal() {
		return false;
	}	
	
	public void clearScreen() {		
	}
	
	private void reportError() {
		System.out.printf("Internal Error: Problem reading console input (aborting)..\n");
	    System.out.printf("Try using -DConsole=Basic\n");			
		System.exit(1);		
	}
}
