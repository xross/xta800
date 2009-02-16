package com.xmos.xta.console;

import java.util.Scanner;
import java.util.Vector;


public class BasicConsole implements Console {
	public void display(String str) {
		System.out.printf("%s", str);
	}
	
	public void displayPrompt(String str) {
		System.out.printf("%s", str);
	}

	public String getNextLine(Vector<Command> commandHistory) {
    	Scanner in = new Scanner(System.in);
        return in.nextLine();
	}
	
	public boolean wasInternal() {
		return false;
	}	
	
	public void clearScreen() {		
	}
}
