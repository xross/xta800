package com.xmos.xta.console;

import java.util.Vector;

import com.xmos.xta.console.commands.Command;

public interface Console {
	void display(String str);
	String getNextLine(Vector<Command> commandHistory);
	boolean wasInternal();
}
