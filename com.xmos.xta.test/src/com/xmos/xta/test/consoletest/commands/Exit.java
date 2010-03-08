package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

public class Exit implements Command {
	public String getCommand() {
		Random r = new Random();
		if(r.nextBoolean()){
			return "exit ";
		} else {
			Mess mess = new Mess();
			return "exit " + mess.getCommand();
		}
		
	}
}
