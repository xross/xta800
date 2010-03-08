package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

public class Version implements Command{

	public String getCommand() {
		Random r = new Random();
		if(r.nextBoolean()){
			return "version ";
		} else {
			Mess mess = new Mess();
			return "version " + mess.getCommand();
		}
		
	}

}
