package com.xmos.xta.core.consoletest.commands;

import java.util.Random;

public class Clear implements Command{

	public String getCommand() {
		Random r = new Random();
		if(r.nextBoolean()){
			return "clear ";
		} else {
			Mess mess = new Mess();
			return "clear " + mess.getCommand();
		}
		
	}

}
