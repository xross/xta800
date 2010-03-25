package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

import com.xmos.xta.test.consoletest.ConsoleTest;

public class History implements Command {

	public String getCommand() {
		Random r = new Random();
		
		if(ConsoleTest.allowMess){
		if(r.nextBoolean()){
			return "history ";
		} else {
			Mess mess = new Mess();
			return "history " + mess.getCommand();
		}
		} else {
			return "history ";
		}
		
	}

}
