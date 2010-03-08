package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

public class History implements Command {

	public String getCommand() {
		Random r = new Random();
		if(r.nextBoolean()){
			return "history ";
		} else {
			Mess mess = new Mess();
			return "history " + mess.getCommand();
		}
		
	}

}
