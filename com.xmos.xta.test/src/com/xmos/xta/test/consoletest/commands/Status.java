package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

public class Status implements Command{

	public String getCommand() {
		Random r = new Random();
		if(r.nextBoolean()){
			return "status ";
		} else {
			Mess mess = new Mess();
			return "status " + mess.getCommand();
		}
		
	}

}
