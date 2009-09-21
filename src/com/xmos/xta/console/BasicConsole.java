package com.xmos.xta.console;

import java.util.Scanner;
import java.util.Vector;

import com.xmos.xta.console.commands.Command;

public class BasicConsole extends Console {
	public void display(String str) {
		if (!redirectToFile(str)) {
			System.out.printf("%s", str);
		}
	}

	public void displayPrompt(String str) {
		System.out.printf("%s", str);
	}

	public String getNextLine(Vector<Command> commandHistory) {
    	Scanner in = new Scanner(System.in);
        return in.nextLine();
	}
		
	public void clearScreen() {		
	}
	
	public boolean hasEscaped() {
		return false;
	}
}
