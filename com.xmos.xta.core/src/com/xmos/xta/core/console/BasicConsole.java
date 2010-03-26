package com.xmos.xta.core.console;

import java.util.Scanner;
import java.util.Vector;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.CommandSource.Type;

public class BasicConsole extends Console {

	public BasicConsole(){
    	//System.out.println("do it basic console" );
	}
	
	public void display(String str) {
		if (!redirectToFile(str)) {
			System.out.printf("%s", str);
		}
	}

	public void displayPrompt(String str) {
		System.out.printf("%s", str);
	}

	public ConsoleCommand getNextLine(Vector<Command> commandHistory) {
    	Scanner in = new Scanner(System.in);
        String command = in.nextLine();
        CommandSource src = new CommandSource(Type.CONSOLE);
        return new ConsoleCommand(command, src);
	}

	public void clearScreen() {
	}

	public boolean hasEscaped() {
		return false;
	}
}
