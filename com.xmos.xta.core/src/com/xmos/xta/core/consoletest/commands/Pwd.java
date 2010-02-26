package com.xmos.xta.core.consoletest.commands;

import java.util.Random;

public class Pwd implements Command {

	public String getCommand() {
		Random r = new Random();
		if(r.nextBoolean()){
			return "pwd ";
		} else {
			Mess mess = new Mess();
			return "pwd " + mess.getCommand();
		}
	}

}
