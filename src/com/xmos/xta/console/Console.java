package com.xmos.xta.console;

import java.util.Vector;


public interface Console {
	void display(String str);
	void displayPrompt(String str);
	String getNextLine(Vector<Command> commandHistory);
	boolean wasInternal();
	void clearScreen();
}
